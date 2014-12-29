package com.marcosgarciacasado.ssdatarest;

public class Measure {

    private final String latitude;
    private final String longitude;
    private final Double value;

    public Measure(String latitude, String longitude, Double value) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.value = value;
    }

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public Double getValue() {
		return value;
	}
    
    
}
