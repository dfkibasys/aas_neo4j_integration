package org.eclipse.basyx.kafka.connect.neo4j;

import java.io.IOException;

import org.eclipse.basyx.kafka.connect.neo4j.pebble.HttpRequestBodyGenerator;

public class Neo4jInitializingHttpBodyGenerator {

	public static void main(String[] args) throws IOException {
		HttpRequestBodyGenerator generator = new HttpRequestBodyGenerator();
		String constraintRequestBodyAsYaml = generator.generateYamlForInitialCypherRequests();
		YamlJsonConverter converter = new YamlJsonConverter();
		System.out.print(converter.yamlToJson(constraintRequestBodyAsYaml));
	}
	
}
