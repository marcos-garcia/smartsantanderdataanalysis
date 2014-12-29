package com.marcosgarciacasado.ssrealtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.marcosgarciacasado.ssjsonformatterinterceptor.MeasureExtractorFactory;
import com.marcosgarciacasado.ssjsonformatterinterceptor.SSJsonFormatterInterceptor;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

/**
 * Bolt that emits measure messages from a single sensor message.
 * 
 * @author Marcos García Casado
 *
 */
public class MeasureFlattenerBolt extends BaseRichBolt {

	private SSJsonFormatterInterceptor interceptor = new SSJsonFormatterInterceptor();
	private OutputCollector collector;
	
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		interceptor.initialize();
		this.collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		
		// Collects the json content of the sensor
		String sensor = input.getStringByField("json");
	    JSONParser jParser = new JSONParser();
	    JSONObject jsonData = null;
		try {
			jsonData = (JSONObject) jParser.parse(sensor);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// Gets its sensor data
	    String latitude = (String) jsonData.get("latitude");
	    String longitude = (String) jsonData.get("longitude");
	    String reqTime = (String) jsonData.get("requesttime");
	    
	    // Calculates the quarter of time of the measure
	    DateTimeFormatter dateParser = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	    DateTime date = dateParser.parseDateTime(reqTime);
	    DateTime ndate = date.plusSeconds(date.getSecondOfMinute() * -1).plusMinutes((date.getMinuteOfHour() % 15) * -1);
	    String tag = (String) jsonData.get("tags");
	    
	    // Obtains the sensor measues into a Hashmap
	    HashMap<String, Double> measures = MeasureExtractorFactory.getMeasureExtractor(tag).getArrayMeasures(jsonData);
	    
	    // For each measure, emits a single message to the next layer
	    for(Entry<String, Double> measure : measures.entrySet()){
	    	List<Object> fields = new ArrayList<Object>();
	    	fields.add(latitude);
	    	fields.add(longitude);
	    	fields.add(dateParser.print(ndate));
	    	fields.add(measure.getKey());
	    	fields.add(measure.getValue());
	    	collector.emit(fields);
	    }		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("latitude","longitude", "date", "measure", "value"));
	}

}
