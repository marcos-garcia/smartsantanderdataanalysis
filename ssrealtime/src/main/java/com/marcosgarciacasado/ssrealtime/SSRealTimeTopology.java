package com.marcosgarciacasado.ssrealtime;

import java.io.Serializable;
import java.util.ArrayList;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

import backtype.storm.Config;
import backtype.storm.generated.StormTopology;
import backtype.storm.LocalCluster;
import backtype.storm.scheduler.Cluster;
import backtype.storm.spout.MultiScheme;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.testing.IdentityBolt;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

/**
 * Storm Topology for the continuous aggregation process.
 * 
 * @author Marcos García Casado
 *
 */
public class SSRealTimeTopology implements Serializable{
	private StormTopology topology;
	private LocalCluster cluster;
	private Config conf;

	public SSRealTimeTopology() {

		// Topology configuration.
		TopologyBuilder builder;
		
		// Kafka Spout configuration
		BrokerHosts brokerHosts = new ZkHosts("localhost:2181");
		SpoutConfig kafkaConfig = new SpoutConfig(brokerHosts, "ssdata", "/storm-kafka", "storm-kafka");
        kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		KafkaSpout kafkaSpout = new KafkaSpout(kafkaConfig);
		
		// Topology build steps
		builder = new TopologyBuilder();
		conf = new Config();
		builder.setSpout("kafka-reader", kafkaSpout, 1);
		builder.setBolt("json-formatter", new JsonFormatterBolt(), 1).shuffleGrouping(
				"kafka-reader");
		builder.setBolt("measure-flattener", new MeasureFlattenerBolt(), 1).shuffleGrouping(
				"json-formatter");
		builder.setBolt("continuous-aggregator", new ContinuousAggregatorBolt(), 1).shuffleGrouping(
				"measure-flattener");
		builder.setBolt("cassandra-save", new CassandraSaveBolt(), 1).shuffleGrouping(
				"continuous-aggregator");
		topology =  builder.createTopology();
	}

	public void runLocal(int runTime) {
		// Local execution of the process
		conf.setDebug(true);
		cluster = new LocalCluster();
		cluster.submitTopology("test", conf, topology);
		if (runTime > 0) {
			Utils.sleep(runTime);
			shutDownLocal();
		}
	}

	public void shutDownLocal() {
		// Termination for the local process
		if (cluster != null) {
			cluster.killTopology("test");
			cluster.shutdown();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Initialization and local process launch
		SSRealTimeTopology topology = new SSRealTimeTopology();
		topology.runLocal(10000);
	}

}
