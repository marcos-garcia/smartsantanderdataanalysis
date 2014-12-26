package com.marcosgarciacasado.ssjsonformatterinterceptor;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

public class AirMeasureExtractor extends MeasureExtractor {

	public AirMeasureExtractor() {
		super();
		measureRegexps.put("co-index", ".*Co Index: ([^<]*)");
		measureRegexps.put("temperature", ".*Temperature: ([^<]*)");
	}

	@Override
	public HashMap<String, Double> getArrayMeasures(JSONObject jsonContent) {
		HashMap<String,Double> i = new HashMap<String,Double>();
		
		fillMeasures("co-index", jsonContent, realPattern, i);
		fillMeasures("temperature", jsonContent, realPattern, i);

		return i;
	}

}
