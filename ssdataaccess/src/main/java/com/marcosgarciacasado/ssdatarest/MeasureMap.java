package com.marcosgarciacasado.ssdatarest;

import java.util.ArrayList;
import java.util.List;

public class MeasureMap {

    private final String measure;
    private final String time;
    private List<Measure> measures;

    public MeasureMap(String measure, String time) {
        this.measure = measure;
        this.time = time;
    }

    private void requestMeasures() {
		SSDBFacade dbf = SSDBFacade.getInstance();
		dbf.connect();
		measures = dbf.getMapMeasures(measure, time);
	}

	public String getMeasure() {
        return measure;
    }

    public String getTime() {
    	return time;
    }

	public List<Measure> getMeasures() {
		if(measures == null){
			requestMeasures();
		}
		return measures;
	}
}
