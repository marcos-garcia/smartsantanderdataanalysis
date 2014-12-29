package com.marcosgarciacasado.ssbatch

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import com.marcosgarciacasado.ssjsonformatterinterceptor.MeasureExtractorFactory
import com.marcosgarciacasado.ssjsonformatterinterceptor.MeasureExtractor
import java.util.HashMap
import scala.collection.JavaConversions._
import scala.collection.mutable.MutableList
import org.joda.time.format.DateTimeFormat
import org.apache.spark.rdd.PairRDDFunctions
import com.esotericsoftware.kryo.Kryo
import org.apache.spark.serializer.KryoRegistrator
import java.io.File
import java.io.PrintWriter
import com.datastax.spark.connector._
import java.util.SortedMap
import java.util.TreeMap
import org.apache.spark.api.java.JavaDoubleRDD
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import java.util.ArrayList

/**
 * Job Spark which calculates the aggregated means of the stored measures and saves them into a Cassandra DB.
 * 
 * @author Marcos García
 *
 */
object SSBatchProcess {

  def main(args: Array[String]): Unit = {

  // Configuration of the job environment  
  val conf = new SparkConf().setAppName("SSBatchProcess")
  conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  conf.set("spark.kryo.registrator", "com.marcosgarciacasado.ssbatch.SSBatchKyroRegistrator")
  conf.set("spark.cassandra.connection.host", "127.0.0.1")
  conf.set("spark.executor.memory", "2g")
  conf.set("spark.storage.memoryFraction", "0.3")
  val sc = new SparkContext(conf)

  // Location of the files
  val path = "hdfs://localhost.localdomain:8020/user/cloudera/SSData/*/*/*";
  val measures = sc.textFile(path);

  // flatMap to parse the measures of each sensor and to get the quarter of hour.
  val flatMeasures = measures.sample(true, 0.1).flatMap { sensor =>
    val l: MutableList[(AggregatedMeasure, AggregatedTempValue)] = MutableList();
    
    // Parsing the JSON options
    val jParser = new JSONParser();
    val jsonData = jParser.parse(sensor).asInstanceOf[JSONObject];
    val latitude = jsonData.get("latitude").asInstanceOf[String];
    val longitude = jsonData.get("longitude").asInstanceOf[String];
    val reqTime = jsonData.get("requesttime").asInstanceOf[String];
    
    // Calculating the quarter of hour
    val dateParser = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
    val date = dateParser.parseDateTime(reqTime)
    val ndate = date.plusSeconds(date.getSecondOfMinute() * -1).plusMinutes((date.getMinuteOfHour() % 15) * -1)
    val tag = jsonData.asInstanceOf[JSONObject].get("tags").asInstanceOf[String];
    
    // Extraction of the sensor measures
    val measures = MeasureExtractorFactory.getMeasureExtractor(tag).getArrayMeasures(jsonData);
    
    // A row will be generated for each measure 
    (for ((k, v) <- measures)
      if (k == "temperature")
      l.+=((new AggregatedMeasure(latitude, longitude, dateParser.print(ndate), k), new AggregatedTempValue(v, 1.0))));
    l
  };
  
  // Erase the measures RDD from memory
  measures.unpersist(false);

  // Obtains the measures mean and standard deviation.
  var measuresStats = sc.cassandraTable("ssda", "ss_measures_stats").select("measure", "mean", "stddev").toJavaRDD()
    .rdd.map(r => (r.getString("measure"), r.getDouble("mean"), r.getDouble("stddev"))) ;
  
  var means : HashMap[String,Double] = new HashMap[String,Double]()
  var stddevs : HashMap[String,Double] = new HashMap[String,Double]()
  
  
  measuresStats.collect().foreach{r => 
  	means.put(r._1 , r._2 )
  	stddevs.put(r._1 , r._3 )
  }

  // Filters the data to keep only the measures between mean - 2 * stddev and mean + 2 * stddev 
  var cleanMeasures = flatMeasures.filter(m =>
    Math.abs(m._2.getMean() - means.get(m._1.measure)  ) < 2 * stddevs.get(m._1.measure) 
  )
  val pairMeasures = new PairRDDFunctions(cleanMeasures)
  // Aggregation of the data. Stores the sum and the count operation in order to do the average later.
  val reducedMeasures = pairMeasures.reduceByKey((v1, v2) => new AggregatedTempValue(v1.sum + v2.sum, v1.count + v2.count));
  
  
  // Saving the result into ssda.ss_measures_agr
  reducedMeasures.map(i => 
    (i._1.measure,
      i._1.requestTime,
      i._1.latitude,
      i._1.longitude,
      i._2.getMean())
  ).saveToCassandra("ssda", "ss_measures_agr", Seq("measure", "time", "latitude", "longitude", "value"))  
 
  // Saving the result again into ssda.ss_measures_ts_agr
  reducedMeasures.map(i => 
    (i._1.measure,
      i._1.latitude,
      i._1.longitude,
      i._1.requestTime,
      i._2.getMean())
  ).saveToCassandra("ssda", "ss_measures_ts_agr", Seq("measure", "latitude", "longitude", "time", "value"))  

}
  
}