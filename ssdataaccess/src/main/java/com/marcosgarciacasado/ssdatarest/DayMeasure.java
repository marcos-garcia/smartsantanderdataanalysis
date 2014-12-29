package com.marcosgarciacasado.ssdatarest;

import java.util.List;

public class DayMeasure {

    private final String measure;
    private final String day;
    private final List<Double> values;

    public DayMeasure(String measure, String day, List<Double> values) {
        this.measure = measure;
        this.day = day;
        this.values = values;
    }

	public String getMeasure() {
		return measure;
	}

	public String getDay() {
		return day;
	}

	public List<Double> getValues() {
		return values;
	}    
    
}
