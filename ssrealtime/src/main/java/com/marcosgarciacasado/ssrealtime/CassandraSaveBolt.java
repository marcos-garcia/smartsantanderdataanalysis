package com.marcosgarciacasado.ssrealtime;

import java.util.Map;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

/**
 * Bolt that saves the measures into the Cassandra ss_measures_agr table.
 * 
 * @author Marcos García Casado
 *
 */
public class CassandraSaveBolt extends BaseRichBolt {
	
	private Cluster cluster;
	private Session session;

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		if(session == null){
			// Initialization for the database connection.
			cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
			session = cluster.connect("ssda");
		}
	}

	@Override
	public void execute(Tuple input) {
		// Obtains the input values
		String measure   = input.getStringByField("measure");
		String latitude  = input.getStringByField("latitude");
		String longitude = input.getStringByField("longitude");
		String time = input.getStringByField("time"); 
		Double value = input.getDoubleByField("value"); 
		
		// Execution of the CQL INSERT query
		String qry =  
				  "INSERT INTO ss_measures_agr (measure, time, latitude, longitude, value) " +
			      "VALUES (" +
		          "'"+measure+"'," +
		          "'"+time+"'," +
		          "'"+latitude+"'," +
		          "'"+longitude+"'," +
		          value +
		          ")" +
		          ";";
		
		session.execute(qry);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

	@Override
	public void cleanup() {
		if(cluster != null){
			// Database disconnection
			cluster.close();
		}
		super.cleanup();
	}

}
