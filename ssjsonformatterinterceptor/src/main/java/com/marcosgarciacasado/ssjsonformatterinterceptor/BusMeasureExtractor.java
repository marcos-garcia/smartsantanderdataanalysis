package com.marcosgarciacasado.ssjsonformatterinterceptor;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

public class BusMeasureExtractor extends MeasureExtractor {

	public BusMeasureExtractor() {
		super();
		measureRegexps.put("co-index", ".*CO: ([^<]*)");
		measureRegexps.put("temperature", ".*Temperature: ([^<]*)");
		measureRegexps.put("altitude", ".*Altitude: ([^<]*)");
		measureRegexps.put("speed", ".*Speed: ([^<]*)");
		measureRegexps.put("course", ".*Course: ([^<]*)");
		measureRegexps.put("odometer", ".*Odometer: ([^<]*)");
		measureRegexps.put("particles", ".*Particles: ([^<]*)");
		measureRegexps.put("humidity", ".*Humidity: ([^<]*)");
	}

	@Override
	public HashMap<String, Double> getArrayMeasures(JSONObject jsonContent) {
		HashMap<String,Double> i = new HashMap<String,Double>();
		
		fillMeasures("co-index", jsonContent, realPattern, i);
		fillMeasures("temperature", jsonContent, realPattern, i);
		fillMeasures("altitude", jsonContent, realPattern, i);
		fillMeasures("speed", jsonContent, realPattern, i);
		fillMeasures("course", jsonContent, realPattern, i);
		fillMeasures("odometer", jsonContent, realPattern, i);
		fillMeasures("particles", jsonContent, realPattern, i);
		fillMeasures("humidity", jsonContent, realPattern, i);

		return i;
	}
}
