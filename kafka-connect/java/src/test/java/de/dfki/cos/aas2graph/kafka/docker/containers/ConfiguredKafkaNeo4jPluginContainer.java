package de.dfki.cos.aas2graph.kafka.docker.containers;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

public class ConfiguredKafkaNeo4jPluginContainer extends GenericContainer<ConfiguredKafkaNeo4jPluginContainer> {

	
	
	public ConfiguredKafkaNeo4jPluginContainer(Network network, ConfiguredKafkaContainer kafka, ConfiguredNeo4jContainer neo4j) {
		super(dockerFile());
		// could take longer to build
		withStartupTimeout(Duration.ofMinutes(2));
		withEnv(envVars()).withNetwork(network).withNetworkAliases("neo4j-kafka-connect").dependsOn(List.of(kafka, neo4j)).withReuse(true).waitingFor(Wait.forLogMessage(".*neo4j-kafka-connect plugin is ready to use.*", 1))
				.withLogConsumer(o -> {
					System.err.println(o.getUtf8String());
				});

	}

	private static ImageFromDockerfile dockerFile() {
		return new ImageFromDockerfile("dfkibasys/kafka-connect-neo4j:test", true).withDockerfile(Paths.get("../Dockerfile"));
	}

	private Map<String, String> envVars() {
		return Map.of("CONNECT_BOOTSTRAP_SERVERS", "kafka:9092", "CONNECT_REST_ADVERTISED_HOST_NAME", "kafka-connect", "CONNECT_LOG4J_LOGGERS",
				"org.eclipse.basyx.kafka.connect.neo4j=DEBUG,org.apache.kafka.connect=INFO,org.apache.kafka.connect.runtime=INFO,io.pebbletemplates.pebble=TRACE", "BASYX_AAS_REPO_URL", "http://aas-environment:8081",
				"BASYX_NEO4J_TARGET_URL", "http://neo4j:7474/db/neo4j/tx/commit");
	}

}