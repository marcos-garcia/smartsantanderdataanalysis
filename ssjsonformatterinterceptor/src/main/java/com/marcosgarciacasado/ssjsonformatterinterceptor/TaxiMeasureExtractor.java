package com.marcosgarciacasado.ssjsonformatterinterceptor;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class TaxiMeasureExtractor extends MeasureExtractor {

	public TaxiMeasureExtractor() {
		super();
		measureRegexps.put("altitude", ".*Altitude: ([^<]*)");
		measureRegexps.put("speed", ".*>Speed: ([^<]*)");
		measureRegexps.put("course", ".*>Course: ([^<]*)");
		measureRegexps.put("odometer", ".*>Odometer: ([^<]*)");
		measureRegexps.put("co-index", ".*CO: ([^<]*)");
		measureRegexps.put("particles", ".*>Particles: ([^<]*)");
		measureRegexps.put("humidity", ".*>Humidity: ([^<]*)");
	}

	@Override
	public HashMap<String, Double> getArrayMeasures(JSONObject jsonContent) {
		HashMap<String,Double> i = new HashMap<String,Double>();
		
		fillMeasures("altitude", jsonContent, realPattern, i);
		fillMeasures("speed", jsonContent, realPattern, i);
		fillMeasures("course", jsonContent, realPattern, i);
		fillMeasures("odometer", jsonContent, realPattern, i);
		fillMeasures("co-index", jsonContent, realPattern, i);
		fillMeasures("particles", jsonContent, realPattern, i);
		fillMeasures("humidity", jsonContent, realPattern, i);

		return i;
	}

}
