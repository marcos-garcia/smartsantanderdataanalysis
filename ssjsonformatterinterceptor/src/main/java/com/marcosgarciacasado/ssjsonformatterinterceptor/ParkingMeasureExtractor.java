package com.marcosgarciacasado.ssjsonformatterinterceptor;

import java.util.HashMap;
import java.util.regex.Matcher;

import org.json.simple.JSONObject;

public class ParkingMeasureExtractor extends MeasureExtractor {

	public ParkingMeasureExtractor() {
		super();
		measureRegexps.clear();
		measureRegexps.put("parking", ".*(parking)2\\.png|(parking_off)2\\.png.*");
	}

	@Override
	public HashMap<String, Double> getArrayMeasures(JSONObject jsonContent) {
		HashMap<String,Double> i = new HashMap<String,Double>();
		
		String tem = (String)jsonContent.get("parking");
		if(tem != null){
			Double p = 0.0;
			if(tem == "parking"){
				p = 1.0;
			}
			i.put("parking", p);
		}
		return i;
	}

}
