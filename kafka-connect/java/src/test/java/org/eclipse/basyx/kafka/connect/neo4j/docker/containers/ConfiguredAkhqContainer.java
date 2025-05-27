package org.eclipse.basyx.kafka.connect.neo4j.docker.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.MountableFile;

public class ConfiguredAkhqContainer extends GenericContainer<ConfiguredAkhqContainer> {

	private static final String NETWORK_ALIAS = "akhq";

	public ConfiguredAkhqContainer(Network network, ConfiguredKafkaContainer kafka) {
		super("tchiotludo/akhq:0.25.1");
		withCopyToContainer(MountableFile.forHostPath("./src/test/resources/akhq.yml"), "/app/application.yml");
//		withEnv("AKHQ_CONFIGURATION", "akhq.connections.docker-kafka-server.properties.bootstrap.servers=kafka:9092");
		withExposedPorts(8080).withNetwork(network).withNetworkAliases(NETWORK_ALIAS).withReuse(true);
		withLogConsumer(outputFrame -> {
			System.err.print(outputFrame.getUtf8String());	
		});
	}


}