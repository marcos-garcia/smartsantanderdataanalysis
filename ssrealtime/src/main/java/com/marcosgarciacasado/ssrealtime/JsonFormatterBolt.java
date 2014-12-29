package com.marcosgarciacasado.ssrealtime;

import java.util.ArrayList;
import java.util.Map;

import com.marcosgarciacasado.ssjsonformatterinterceptor.SSJsonFormatterInterceptor;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

/**
 * Bolt that formats the sensor measures into a JSON object format
 * 
 * @author Marcos García Casado
 *
 */
public class JsonFormatterBolt extends BaseRichBolt {

	private SSJsonFormatterInterceptor interceptor = new SSJsonFormatterInterceptor();
	private OutputCollector collector;
	
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		// Initialization of the interceptor formatter object
		interceptor.initialize();
		this.collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		// Gets the content and passes it through the format function.
		String originalContent = input.getStringByField("str");
		String formattedContent = interceptor.formatToJSON(originalContent);
		// If there are values on the string result, emits the JSON object into the next 
		// bolt layers
		if (formattedContent != "") {
			ArrayList<Object> a = new ArrayList<Object>();
			a.add(formattedContent);
			collector.emit(a);
		}
		
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("json"));

	}

}
