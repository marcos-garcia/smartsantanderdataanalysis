package com.marcosgarciacasado.ssjsonformatterinterceptor;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class TempMeasureExtractor extends MeasureExtractor {

	public TempMeasureExtractor() {
		super();
		measureRegexps.put("temperature", ".*Temperature: ([^<]*)");
	}

	@Override
	public HashMap<String, Double> getArrayMeasures(JSONObject jsonContent) {
		HashMap<String,Double> i = new HashMap<String,Double>();
		
		fillMeasures("temperature", jsonContent, realPattern, i);

		return i;
	}

}
