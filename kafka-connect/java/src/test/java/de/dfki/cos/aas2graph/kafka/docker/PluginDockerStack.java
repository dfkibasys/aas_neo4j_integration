package de.dfki.cos.aas2graph.kafka.docker;

import java.util.List;

import org.testcontainers.containers.Network;
import org.testcontainers.lifecycle.Startable;
import org.testcontainers.lifecycle.Startables;

import de.dfki.cos.aas2graph.kafka.docker.containers.ConfiguredAasEnvironmentContainer;
import de.dfki.cos.aas2graph.kafka.docker.containers.ConfiguredKafkaContainer;
import de.dfki.cos.aas2graph.kafka.docker.containers.ConfiguredKafkaNeo4jPluginContainer;
import de.dfki.cos.aas2graph.kafka.docker.containers.ConfiguredNeo4jContainer;

public class PluginDockerStack implements Startable {
	
	private final Network network = Network.newNetwork();

	private final ConfiguredKafkaContainer kafka = new ConfiguredKafkaContainer(network);
	private final ConfiguredNeo4jContainer neo4j = new ConfiguredNeo4jContainer(network);
	private final ConfiguredAasEnvironmentContainer aasEnv = new ConfiguredAasEnvironmentContainer(network, kafka);
	private final ConfiguredKafkaNeo4jPluginContainer connect = new ConfiguredKafkaNeo4jPluginContainer(network, kafka, neo4j);
//	private final ConfiguredAkhqContainer akhq = new ConfiguredAkhqContainer(network, kafka);	
	
	
	@Override
	public void start() {
		Startables.deepStart(List.of(kafka, neo4j, aasEnv, connect)).join();
	}
	
	@Override
	public void stop() {
		
		connect.stop();
		aasEnv.stop();
		neo4j.stop();
	//	akhq.stop();
		kafka.stop();
	}

	public ConfiguredAasEnvironmentContainer aasEnv() {
		return aasEnv;
	}

	public ConfiguredNeo4jContainer neo4j() {
		return neo4j;
	}

	public ConfiguredKafkaNeo4jPluginContainer connect() {
		return connect;
	}

	public ConfiguredKafkaContainer kafka() {
		return kafka;
	}	
	

}
