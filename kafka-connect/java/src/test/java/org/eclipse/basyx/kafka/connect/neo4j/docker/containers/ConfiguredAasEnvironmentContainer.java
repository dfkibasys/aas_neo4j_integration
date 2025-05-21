package org.eclipse.basyx.kafka.connect.neo4j.docker.containers;

import java.util.Map;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.lifecycle.Startable;

public class ConfiguredAasEnvironmentContainer extends GenericContainer<ConfiguredAasEnvironmentContainer> {

	private static final String NETWORK_ALIAS = "aas-env";

	public ConfiguredAasEnvironmentContainer(Network network, Startable kafka) {
		super("eclipsebasyx/aas-environment:2.0.0-SNAPSHOT-d81b59c");

		withNetwork(network).withNetworkAliases(NETWORK_ALIAS).withReuse(true).withExposedPorts(8081).dependsOn(kafka)
				.withEnv(envVars());
	}


	public void enableCommandLineLogging() {
		withLogConsumer(o -> System.out.println(o.getUtf8StringWithoutLineEnding()));
	}

	private Map<String, String> envVars() {
		return Map.of("BASYX_FEATURE_KAFKA_ENABLED", "true", "SPRING_KAFKA_BOOTSTRAP_SERVERS", "PLAINTEXT://kafka:9092",
				"BASYX_EXTERNALURL", "http://localhost:8081,http://aas-environment:8081");
	}

	public String urlAsString() {
		return "http://localhost:" + getFirstMappedPort();
	}

}
