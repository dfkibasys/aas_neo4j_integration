package org.eclipse.basyx.kafka.connect.neo4j.docker.containers;

import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class ConfiguredNeo4jContainer extends Neo4jContainer<ConfiguredNeo4jContainer> {

	private static final String NETWORK_ALIAS = "neo4j";

	public ConfiguredNeo4jContainer(Network network) {
		super(DockerImageName.parse("neo4j:5.23.0-community-bullseye"));
		withEnv("NEO4J_dbms_logs_debug_level", "DEBUG").withExposedPorts(7474, 7687).withoutAuthentication()
				.withNetwork(network).waitingFor(Wait.forLogMessage(".*Started.*", 1)).withNetworkAliases(NETWORK_ALIAS)
				.withReuse(true);
	}

	public String getTransactionalHttpUrl() {
		return getHttpUrl() + "/db/neo4j/tx/commit";
	}
}
