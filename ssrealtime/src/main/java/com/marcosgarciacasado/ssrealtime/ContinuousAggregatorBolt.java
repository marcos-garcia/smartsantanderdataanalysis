package com.marcosgarciacasado.ssrealtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

/**
 * Bolt for the continuous aggregation of the measures.
 * 
 * @author Marcos García Casado
 *
 */
public class ContinuousAggregatorBolt extends BaseRichBolt {

	private OutputCollector collector;
	private HashMap<String,String> timeController;
	private HashMap<String,Double> valueController;
	private HashMap<String,Integer> measureCountController;
	
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		// Hashmaps initialization
		this.collector = collector;
		this.timeController = new HashMap<String,String>(); // "medida:latitud:longitud,tiempo"
		this.valueController = new HashMap<String,Double>(); // "medida:latitud:longitud,valor"
		this.measureCountController = new HashMap<String,Integer>(); // "medida:latitud:longitud,0"
	}

	@Override
	public void execute(Tuple input) {
		// Obtains the input values
		String measure   = input.getStringByField("measure");
		String latitude  = input.getStringByField("latitude");
		String longitude = input.getStringByField("longitude");
		String time = input.getStringByField("time"); 
		Double value = input.getDoubleByField("value"); 
		String key = measure + ":" + latitude + ":" + longitude;
		
		// 1. Checks if the measure already exists
		if(!timeController.containsKey(key) || timeController.get(key) != time){
			// If it does not exist before, adds the measure starting with a zero value 
			// for both the sum and count of the values
			timeController.put(key, time);
			valueController.put(key, 0.0);
			measureCountController.put(key, 0);
		}
		// 2. Updates the aggregation value and emits it to the next bolt layers
		valueController.put(key, value + valueController.get(key));
		measureCountController.put(key, 1 + measureCountController.get(key));
		double mean = valueController.get(key) / measureCountController.get(key);
		  	List<Object> fields = new ArrayList<Object>();
	    	fields.add(latitude);
	    	fields.add(longitude);
	    	fields.add(time);
	    	fields.add(measure);
	    	fields.add(mean);
	    	collector.emit(fields);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("latitude","longitude", "date", "measure", "value"));
	}

}
