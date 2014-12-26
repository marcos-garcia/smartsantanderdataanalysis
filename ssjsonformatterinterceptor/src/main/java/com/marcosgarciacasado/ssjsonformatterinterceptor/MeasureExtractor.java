package com.marcosgarciacasado.ssjsonformatterinterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * Abstract class which manages the extraction of measure values from a JSON object containing a sensor status.
 * 
 * @author Marcos García Casado
 *
 */
public class MeasureExtractor {

	// Collection of regular expressions for value extraction
	protected Map<String,String> measureRegexps;
	
	protected Pattern realPattern = Pattern.compile("(-?[\\d\\.]*)");

	public MeasureExtractor(){
		measureRegexps = new HashMap<String,String>();
		
		// Common measures
		measureRegexps.put("last-update", ".*Last update: ([^<]*)");
		measureRegexps.put("battery-level", ".*Battery level: ([^<]*)");
	}

	/**
	 * Obtains a JSONArray with the measures extracted from a string passed by argument.
	 * 
	 * @author Marcos García Casado
	 *
	 */
	public JSONArray getJSONMeasures(String content){
		JSONArray ja = new JSONArray();
		
		for(Entry<String, String> measure : measureRegexps.entrySet()){
			Pattern r = Pattern.compile(measure.getValue());
			Matcher m = r.matcher(content);
			if(m.find()){
				JSONObject jsonMeasure = new JSONObject();
				jsonMeasure.put(measure.getKey(), m.group(1));
				ja.add(jsonMeasure);
			}
		}

		return ja;
	}

	/**
	 * Obtains a HashMap with the measures extracted from a JSONObject representing the sensor data.
	 *
	 * @author Marcos García Casado
	 *
	 */
	public HashMap<String,Double> getArrayMeasures(JSONObject jsonContent){
		HashMap<String,Double> i = new HashMap<String,Double>();
		return i;
	}

	/**
	 * Extracts a pattern from a measure value located in a JSONObject and stores it in a HashMap whether finds it.
	 *
	 * @author Marcos García Casado
	 *
	 */
	public void fillMeasures(String measure, JSONObject jsonContent, Pattern pattern, HashMap<String,Double> i){
		String tem = (String)jsonContent.get(measure);
		if(tem != null){
			Matcher tM = pattern.matcher(tem);
			if(tM.find() && !tM.group(1).isEmpty()){
				try{
					i.put(measure, Double.valueOf(tM.group(1)));
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
			}
		}
	}
}
