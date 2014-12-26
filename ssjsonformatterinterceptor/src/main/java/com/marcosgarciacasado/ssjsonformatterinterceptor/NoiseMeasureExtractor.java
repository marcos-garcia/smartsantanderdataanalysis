package com.marcosgarciacasado.ssjsonformatterinterceptor;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class NoiseMeasureExtractor extends MeasureExtractor {

	public NoiseMeasureExtractor() {
		super();
		measureRegexps.put("noise", ".*Noise: ([^<]*)");
	}

	@Override
	public HashMap<String, Double> getArrayMeasures(JSONObject jsonContent) {
		HashMap<String,Double> i = new HashMap<String,Double>();
		
		fillMeasures("noise", jsonContent, realPattern, i);

		return i;
	}

}
