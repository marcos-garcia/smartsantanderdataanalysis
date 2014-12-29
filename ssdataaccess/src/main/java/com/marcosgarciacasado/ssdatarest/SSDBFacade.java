package com.marcosgarciacasado.ssdatarest;

import java.util.ArrayList;
import java.util.List;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class SSDBFacade {

	private static SSDBFacade instance;
	
	private Cluster cluster;
	private Session session;

	private SSDBFacade(){
	}
	
	public static SSDBFacade getInstance(){
		if(instance == null){
			instance = new SSDBFacade();
		}
		return instance;
	}
	
	
	public void connect(){
		if(session == null){
			cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
			session = cluster.connect("ssda");
		}
	}
	
	public List<Measure> getMapMeasures(String measure, String time){
		List<Measure> list = new ArrayList<Measure>();
		String qry = 
				"SELECT latitude, longitude, value " +
				"FROM ss_measures_agr " +
				"WHERE measure = '"+measure+"' " +
				"  AND time    = '"+   time+"' ";
		ResultSet results = session.execute(qry);
		for (Row row : results) {
			String latitude = row.getString("latitude");
			String longitude = row.getString("longitude");
			Double value = row.getDouble("value");
			list.add(new Measure(latitude, longitude, value));
		}
		return list;
	}
	
	public List<MeasureStat> getMeasureStats(){
		List<MeasureStat> list = new ArrayList<MeasureStat>();
		String qry = 
				"SELECT * " +
				"FROM ss_measures_stats ";
		ResultSet results = session.execute(qry);
		for (Row row : results) {
			String measure = row.getString("measure");
			Double mean = row.getDouble("mean");
			Double stddev = row.getDouble("stddev");
			list.add(new MeasureStat(measure, mean, stddev));
		}
		return list;
	}	
	
	public List<ClusterResult> getClusterResults(){
		List<ClusterResult> list = new ArrayList<ClusterResult>();
		String qry = 
				"SELECT * " +
				"FROM ss_cluster_res ";
		ResultSet results = session.execute(qry);
		for (Row row : results) {
			String day = row.getString("day");
			Integer clusterid = row.getInt("clusterid");
			list.add(new ClusterResult(day, clusterid));
		}
		return list;
	}	
	
	public List<Double> getClusterCenters(int id){
		List<Double> list = new ArrayList<Double>();
		String qry = 
				"SELECT * " +
				"FROM ss_cluster_centers "+
				"WHERE clusterid = "+id;
		ResultSet results = session.execute(qry);
		Row row = results.iterator().next();
		if(row != null){
			list = row.getList("values", Double.class);
		}
		return list;
	}		
	
	public List<Double> getDayResults(String measure, String day){
		List<Double> list = new ArrayList<Double>();
		String qry = 
				"SELECT * " +
				"FROM ss_measures_ts_agr_02 "+
				"WHERE measure = '"+measure+"' "+
				"  AND day = '"+day+"'";
		ResultSet results = session.execute(qry);
		Row row = results.iterator().next();
		if(row != null){
			list = row.getList("values", Double.class);
		}
		return list;
	}	
	
	protected void finalize() throws Throwable {
		if(cluster != null){
			cluster.close();
		}
		super.finalize();
	}
}
