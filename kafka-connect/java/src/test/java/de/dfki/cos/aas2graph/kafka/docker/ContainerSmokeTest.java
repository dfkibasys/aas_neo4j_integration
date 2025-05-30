package de.dfki.cos.aas2graph.kafka.docker;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Network;

import de.dfki.cos.aas2graph.kafka.docker.containers.ConfiguredAkhqContainer;
import de.dfki.cos.aas2graph.kafka.docker.containers.ConfiguredKafkaContainer;
import de.dfki.cos.aas2graph.kafka.docker.containers.ConfiguredKafkaNeo4jPluginContainer;
import de.dfki.cos.aas2graph.kafka.docker.containers.ConfiguredNeo4jContainer;

public class ContainerSmokeTest {

	@Test
	public void testStackStarts() throws InterruptedException {
		Network network = Network.newNetwork();
		try (ConfiguredKafkaContainer kafka = new ConfiguredKafkaContainer(network);
				ConfiguredAkhqContainer akhq = new ConfiguredAkhqContainer(network, kafka).withExposedPorts(8080);
				ConfiguredNeo4jContainer neo4j = new ConfiguredNeo4jContainer(network).withExposedPorts(7474, 7687);
				ConfiguredKafkaNeo4jPluginContainer plugin = new ConfiguredKafkaNeo4jPluginContainer(network, kafka, neo4j)) {

			kafka.start();
			neo4j.start();
			plugin.start();

			waitForRunning(plugin);
			Assertions.assertTrue(plugin.isRunning());
			assertIndicesDeployed(neo4j);
			plugin.stop();
			neo4j.stop();
			kafka.stop();
		}

	}

	private void assertIndicesDeployed(ConfiguredNeo4jContainer neo4j) {
		String address1 = "http://localhost:" + neo4j.getMappedPort(7474);
		String address2 = "http://localhost:" + neo4j.getMappedPort(7687);
		System.out.println(address1);
		System.out.println(address2);
		System.out.println("===0");
	}

	private void waitForRunning(ConfiguredKafkaNeo4jPluginContainer plugin) throws InterruptedException {
		long start = System.currentTimeMillis();
		long maxWaitTime = Duration.ofSeconds(30).toMillis();

		while (!plugin.isRunning()) {

			if (System.currentTimeMillis() - start > maxWaitTime) {
				System.err.println("Timeout: Plugin is not running within 30 seconds.");
				break;
			}
			System.out.println("sleep");
			Thread.sleep(1000); // wait one second
		}
		System.out.println("running");
	}
}
