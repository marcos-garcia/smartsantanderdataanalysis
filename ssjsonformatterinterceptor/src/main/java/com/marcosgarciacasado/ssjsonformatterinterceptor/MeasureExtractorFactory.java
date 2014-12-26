package com.marcosgarciacasado.ssjsonformatterinterceptor;

public class MeasureExtractorFactory {

	public static MeasureExtractor getMeasureExtractor(String tag) {
		switch (tag) {
		case "air":
			return new AirMeasureExtractor();
		case "light":
			return new LightMeasureExtractor();
		case "noise":
			return new NoiseMeasureExtractor();
		case "temp":
			return new NoiseMeasureExtractor();
		case "irrigation":
			return new IrrigationMeasureExtractor();
		case "agriculture":
			return new AgricultureMeasureExtractor();
		case "env_station":
			return new EnvStationMeasureExtractor();
		case "parquesyjardines":
			return new ParquesJardinesMeasureExtractor();
		case "PARKING":
			return new ParkingMeasureExtractor();
		case "BUS":
			return new BusMeasureExtractor();
		case "MICROBUS":
			return new BusMeasureExtractor();
		case "TAXI":
			return new TaxiMeasureExtractor();
		case "vehicle_speed":
			return new VehicleSpeedMeasureExtractor();
		case "vehicle_counter":
			return new VehicleCounterMeasureExtractor();
		case "panel":
			return new EmptyMeasureExtractor();
		default:
			System.out.println("tag " + tag + " no registrado.");
			return null;
		}
	}
}
