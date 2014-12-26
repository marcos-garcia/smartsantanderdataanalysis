package com.marcosgarciacasado.ssjsonformatterinterceptor;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class IrrigationMeasureExtractor extends MeasureExtractor {

	public IrrigationMeasureExtractor() {
		super();
		measureRegexps.put("relative-humidity", ".*Relative humidity: ([^<]*)");
		measureRegexps.put("soil-moisture", ".*Soil Moisture: ([^<]*)");
		measureRegexps.put("soil-temperature", ".*Soil Temperature: ([^<]*)");
		measureRegexps.put("temperature", ".*>Temperature: ([^<]*)");
	}

	@Override
	public HashMap<String, Double> getArrayMeasures(JSONObject jsonContent) {
		HashMap<String,Double> i = new HashMap<String,Double>();
		
		fillMeasures("relative-humidity", jsonContent, realPattern, i);
		fillMeasures("soil-moisture", jsonContent, realPattern, i);
		fillMeasures("soil-temperature", jsonContent, realPattern, i);
		fillMeasures("temperature", jsonContent, realPattern, i);

		return i;
	}

}
