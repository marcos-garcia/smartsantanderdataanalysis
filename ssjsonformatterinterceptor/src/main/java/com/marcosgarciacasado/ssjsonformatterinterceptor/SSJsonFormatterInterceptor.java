package com.marcosgarciacasado.ssjsonformatterinterceptor;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.collect.Lists;

/**
 * Flume interceptor which formats the sensor read values into JSON objects
 * in order to ease the value readings further.
 * 
 * @author Marcos García Casado
 *
 */
public class SSJsonFormatterInterceptor implements Interceptor, Serializable {

	private HashMap<String, Integer> sensorStatus;

	public void close() {
	}

	public void initialize() {
		sensorStatus = new HashMap<String, Integer>();
	}

	public Event intercept(Event arg0) {
		
		// Receives the message and sends it into the formatter function
		String originalContent = new String(arg0.getBody());
		String formattedContent = formatToJSON(originalContent);
		
		// If there is no information on the result, it does not emits a message forward
		if (formattedContent == "") {
			arg0 = null;
		} else {
			arg0.setBody(formattedContent.getBytes());
		}
		
		return arg0;
	}

	public List<Event> intercept(List<Event> arg0) {
		List<Event> out = Lists.newArrayList();
		for (Event event : arg0) {
			Event outEvent = intercept(event);
			if (outEvent != null) {
				out.add(outEvent);
			}
		}
		return out;
	}

	public String formatToJSON(String content) {
		ArrayList<String> sensorsData = new ArrayList<String>();
		URL url = null;

		JSONParser jparser = new JSONParser();
		JSONObject jsonData = null;
		String requestTs = null;
		String JSONContent = null;
		
		// Separates the date of the web request and its content
		try {
			String[] splittedContent = content.split("~");
			requestTs = splittedContent[0];
			JSONContent = splittedContent[1];
			jsonData = (JSONObject) jparser.parse(JSONContent);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		JSONArray markers = (JSONArray) jsonData.get("markers");
		
		// Prepares the iteration for each sensor
		Iterator<JSONObject> iterator = markers.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			
			// Extracts the characteristics of the sensors
			JSONObject sensorData = iterator.next();
			String tag = (String) sensorData.get("tags");
			String title = (String) sensorData.get("title");
			String sensorContent = (String) sensorData.get("content")
					+ (String) sensorData.get("image");
			int contentHash = sensorContent.hashCode();
			
			// Checks if the sensor information has been updated since last message
			if (sensorStatus.containsKey(title)
					&& sensorStatus.get(title).equals(contentHash)) {
			} else {
				
				// Updates the hashcontent for the next update check
				sensorStatus.put(title, contentHash);
				MeasureExtractor me = MeasureExtractorFactory
						.getMeasureExtractor(tag);
				if (me == null) {
					// Nothing to do
				} else {
					
					// Measures extraction part
					JSONArray newContent = me.getJSONMeasures(sensorContent);
					Iterator<JSONObject> it = newContent.iterator();
					
					// For each measures, writes its value into the JSON Array Object
					while (it.hasNext()) {
						sensorData.putAll(it.next());
						;
					}
					sensorData.remove("content");
					sensorData.remove("image");
					sensorData.put("requesttime", requestTs);
					sensorsData.add(sensorData.toJSONString());
				}
			}
		}
		
		// Returns the JSON object
		String finalData = "";
		for (String sensor : sensorsData) {
			if (finalData.length() > 0) {
				finalData = finalData + "\n";
			}
			finalData = finalData + sensor;
		}
		return finalData;
	}

	public static class Builder implements Interceptor.Builder{
		
		@Override
		public void configure(Context arg0) { }
		
		@Override
		public Interceptor build(){
			return new SSJsonFormatterInterceptor();
		}
	}	
}
