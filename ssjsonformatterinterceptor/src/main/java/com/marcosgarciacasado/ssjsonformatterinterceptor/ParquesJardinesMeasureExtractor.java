package com.marcosgarciacasado.ssjsonformatterinterceptor;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class ParquesJardinesMeasureExtractor extends MeasureExtractor {

	public ParquesJardinesMeasureExtractor() {
		super();
		measureRegexps.put("altitude", ".*Altitude: ([^<]*)");
		measureRegexps.put("speed", ".*>Speed: ([^<]*)");
		measureRegexps.put("course", ".*>Course: ([^<]*)");
		measureRegexps.put("odometer", ".*>Odometer: ([^<]*)");
	}

	@Override
	public HashMap<String, Double> getArrayMeasures(JSONObject jsonContent) {
		HashMap<String,Double> i = new HashMap<String,Double>();
		
		fillMeasures("altitude", jsonContent, realPattern, i);
		fillMeasures("speed", jsonContent, realPattern, i);
		fillMeasures("course", jsonContent, realPattern, i);
		fillMeasures("odometer", jsonContent, realPattern, i);

		return i;
	}

}
