package com.marcosgarciacasado.ssjsonformatterinterceptor;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

public class EnvStationMeasureExtractor extends MeasureExtractor {

	public EnvStationMeasureExtractor() {
		super();
		measureRegexps.put("temperature", ".*>Temperature: ([^<]*)");
		measureRegexps.put("relative-humidity", ".*Relative humidity: ([^<]*)");
		measureRegexps.put("soil-moisture", ".*Soil Moisture: ([^<]*)");
		measureRegexps.put("solar-radiation", ".*Solar Radiation: ([^<]*)");
		measureRegexps.put("rainfall", ".*Rainfall: ([^<]*)");
		measureRegexps.put("wind-speed", ".*Wind Speed: ([^<]*)");
		measureRegexps.put("wind-direction", ".*Wind Direction: ([^<]*)");
		measureRegexps.put("radiation-PAR", ".*Radiation PAR: ([^<]*)");
		measureRegexps.put("atmospheric-pressure", ".*Atmospheric Pressure: ([^<]*)");
	}

	@Override
	public HashMap<String, Double> getArrayMeasures(JSONObject jsonContent) {
		HashMap<String,Double> i = new HashMap<String,Double>();
		
		fillMeasures("temperature", jsonContent, realPattern, i);
		fillMeasures("relative-humidity", jsonContent, realPattern, i);
		fillMeasures("soil-moisture", jsonContent, realPattern, i);
		fillMeasures("solar-radiation", jsonContent, realPattern, i);
		fillMeasures("rainfall", jsonContent, realPattern, i);
		fillMeasures("wind-speed", jsonContent, realPattern, i);
		//fillMeasures("wind-direction", jsonContent, realPattern, i);
		//fillMeasures("radiation-PAR", jsonContent, realPattern, i);
		fillMeasures("atmospheric-pressure", jsonContent, realPattern, i);

		return i;
	}

}
