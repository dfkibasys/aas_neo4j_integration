package org.eclipse.basyx.kafka.connect.neo4j.docker.containers;

import java.util.HashMap;
import java.util.Map;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

public class ConfiguredKafkaContainer extends GenericContainer<ConfiguredKafkaContainer> {

	private static final String NETWORK_ALIAS = "kafka";

	public ConfiguredKafkaContainer(Network network) {
		super("confluentinc/cp-kafka:7.7.1");
		waitingFor(Wait.forLogMessage(".*Kafka Server started.*", 1)).withEnv(envVars()).withNetwork(network)
			.withExposedPorts(9094)
			.withNetworkAliases(NETWORK_ALIAS).withReuse(true);
	}
	
	public String externalBrokerAddress()  {
		return "http://localhost:" + getFirstMappedPort();
	}

	public Map<String, String> envVars() {
		Map<String, String> envVars = new HashMap<>();
		envVars.put("KAFKA_ADVERTISED_LISTENERS", "INTER_BROKER://kafka:9092,EXTERNAL://localhost:9094");
		envVars.put("KAFKA_INTER_BROKER_LISTENER_NAME", "INTER_BROKER");
		envVars.put("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1");
		envVars.put("KAFKA_PROCESS_ROLES", "broker,controller");
		envVars.put("KAFKA_CONTROLLER_LISTENER_NAMES", "CONTROLLER");
		envVars.put("KAFKA_LISTENERS", "INTER_BROKER://:9092,CONTROLLER://:9093,EXTERNAL://:9094");
		envVars.put("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP",
				"CONTROLLER:PLAINTEXT,INTER_BROKER:PLAINTEXT,EXTERNAL:PLAINTEXT");
		envVars.put("KAFKA_BROKER_ID", "1");
		envVars.put("KAFKA_CONTROLLER_QUORUM_VOTERS", "1@127.0.0.1:9093");
		envVars.put("ALLOW_PLAINTEXT_LISTENER", "yes");
		envVars.put("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true");
		envVars.put("KAFKA_CREATE_TOPICS", "aas-registry:1:1,aas-events:1:1,submodel-registry:1:1,dlq-aas-events:1:1");
		envVars.put("CLUSTER_ID", "jmpccZs2RHaYUbZ-LgaIhQ");
		envVars.put("KAFKA_LOG_DIRS", "/var/lib/kafka/data");
		envVars.put("KAFKA_MIN_INSYNC_REPLICAS", "1");
		envVars.put("KAFKA_NUM_PARTITIONS", "1");
		envVars.put("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1");
		envVars.put("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1");
		return envVars;
	}
}
