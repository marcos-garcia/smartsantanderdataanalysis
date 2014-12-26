package com.marcosgarciacasado.ssjsonformatterinterceptor;

import java.util.HashMap;

import org.json.simple.JSONObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AgricultureMeasureExtractor extends MeasureExtractor {

	public AgricultureMeasureExtractor() {
		super();
		measureRegexps.put("relative-humidity", ".*Relative humidity: ([^<]*)");
		measureRegexps.put("temperature", ".*>Temperature: ([^<]*)");
	}

	@Override
	public HashMap<String, Double> getArrayMeasures(JSONObject jsonContent) {
		HashMap<String,Double> i = new HashMap<String,Double>();
		
		fillMeasures("relative-humidity", jsonContent, realPattern, i);
		fillMeasures("temperature", jsonContent, realPattern, i);

		return i;
	}
	
	

}
