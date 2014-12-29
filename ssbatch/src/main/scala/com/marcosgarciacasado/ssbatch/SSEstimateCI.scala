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
 * Spark Job that calculates some statistics for each magnitude.
 * 
 * @author Marcos García Casado
 *
 */
object SSEstimateCI {

  def main(args: Array[String]): Unit = {

  // Configuration of the job environment    
  val conf = new SparkConf().setAppName("SSEstimateCI")
  conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  conf.set("spark.kryo.registrator", "com.marcosgarciacasado.ssbatch.SSBatchKyroRegistrator")
  conf.set("spark.cassandra.connection.host", "localhost")
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
      l.+=((new AggregatedMeasure(latitude, longitude, dateParser.print(ndate), k), new AggregatedTempValue(v, 1.0))));
    l
  };
  
  // Erase the measures RDD from memory
  measures.unpersist(false);

  // Mean calculation
  val meanMeasures = (new PairRDDFunctions(
      flatMeasures.map(measure => (measure._1.measure, new AggregatedTempValue(measure._2.sum, 1.0)))
    ).reduceByKey((v1, v2) => 
      	new AggregatedTempValue(v1.sum + v2.sum, v1.count + v2.count)
    ).map(m => (m._1, m._2.getMean())
  ))
  val hashMeans = meanMeasures;
  
  // Standard deviation calculation
  val stddevMeasures = (
      (new PairRDDFunctions(
		  flatMeasures.map{measure => 
		      var aux = measure._2.sum  - 19.0/*(hashMeans.get(measure._1.measure).get)*/;
		      (measure._1.measure, new AggregatedTempValue(Math.pow(aux,2), 1.0))
		  }))
    .reduceByKey { (v1, v2) =>

      new AggregatedTempValue(v1.sum + v2.sum, v1.count + v2.count)
    }
    .map(m => (m._1, Math.sqrt(m._2.sum / (m._2.count -1) ) )));

  // Saving the statistics into cassandra ssda.ss_measures_stats table.
  val saveMeans = hashMeans.map(r => 
  	(r._1,
    r._2)
  )
  
  val saveStddev = stddevMeasures.map(r => 
  	(r._1,
    r._2)
  )
 saveMeans.saveToCassandra("ssda", "ss_measures_stats", Seq("measure", "mean"))  
 saveStddev.saveToCassandra("ssda", "ss_measures_stats", Seq("measure", "stddev"))  

}
  
}