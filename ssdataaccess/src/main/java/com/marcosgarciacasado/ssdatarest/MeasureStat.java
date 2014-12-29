package com.marcosgarciacasado.ssdatarest;

public class MeasureStat {

    private final String measure;
    private final Double mean;
    private final Double stddev;

    public MeasureStat(String measure, Double mean, Double stddev) {
        this.measure = measure;
        this.mean = mean;
        this.stddev = stddev;
    }

	public String getMeasure() {
		return measure;
	}

	public Double getMean() {
		return mean;
	}

	public Double getStddev() {
		return stddev;
	}
    
}
