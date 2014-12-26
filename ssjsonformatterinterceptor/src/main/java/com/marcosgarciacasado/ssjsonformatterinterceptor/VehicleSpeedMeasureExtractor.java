package com.marcosgarciacasado.ssjsonformatterinterceptor;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class VehicleSpeedMeasureExtractor extends MeasureExtractor {

	public VehicleSpeedMeasureExtractor() {
		super();
		measureRegexps.put("occupancy", ".*Occupancy: ([^<]*)");
		measureRegexps.put("count", ".*Count:([^<]*)");
	}

	@Override
	public HashMap<String, Double> getArrayMeasures(JSONObject jsonContent) {
		HashMap<String,Double> i = new HashMap<String,Double>();
		
		fillMeasures("occupancy", jsonContent, realPattern, i);
		fillMeasures("count", jsonContent, realPattern, i);

		return i;
	}

}
