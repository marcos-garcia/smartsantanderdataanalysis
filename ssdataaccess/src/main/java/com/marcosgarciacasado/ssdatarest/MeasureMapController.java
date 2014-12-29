package com.marcosgarciacasado.ssdatarest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeasureMapController {

    @RequestMapping("/measuremap")
    public List<Measure> measuremap(
    		  @RequestParam(value="measure", required=true, defaultValue="temperature") String measure, 
    		  @RequestParam(value="time",    required=true, defaultValue="2014-07-18 08:45:00") 			String time ) {
    	MeasureMap map = new MeasureMap(measure, time);
    	return map.getMeasures();
    }

    @RequestMapping("/measurestats")
    public List<MeasureStat> measurestats( ) {
		SSDBFacade dbf = SSDBFacade.getInstance();
		dbf.connect();
		List<MeasureStat> stats = dbf.getMeasureStats();
    	return stats;
    }

    @RequestMapping("/clusterresults")
    public List<ClusterResult> clusterresults( ) {
		SSDBFacade dbf = SSDBFacade.getInstance();
		dbf.connect();
		List<ClusterResult> results = dbf.getClusterResults();
    	return results;
    }

    @RequestMapping("/clustercenter")
    public List<Double> clustercenter(
    		  @RequestParam(value="clusterid", required=true, defaultValue="1") int clusterid ) {
    	SSDBFacade dbf = SSDBFacade.getInstance();
		dbf.connect();
		List<Double> results = dbf.getClusterCenters(clusterid);
    	return results;
    }

    @RequestMapping("/dayresults")
    public List<Double> dayresults(
  		  @RequestParam(value="measure", required=true, defaultValue="temperature") String measure, 
  		  @RequestParam(value="day",    required=true,  defaultValue="2014-07-18 00:00:00") String day ) {
    	SSDBFacade dbf = SSDBFacade.getInstance();
		dbf.connect();
		List<Double> results = dbf.getDayResults(measure, day);
    	return results;
    }
}
