package org.eclipse.basyx.kafka.connect.neo4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.basyx.kafka.connect.neo4j.pebble.HttpRequestBodyGenerator;

public class Neo4jInitializingHttpBodyGenerator {

	
	
	
	public static void main(String[] args) throws IOException {
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));

        HttpRequestBodyGenerator generator = new HttpRequestBodyGenerator();
		String constraintRequestBodyAsYaml = generator.generateYamlForInitialCypherRequests();
		YamlJsonConverter converter = new YamlJsonConverter();
		String result = converter.yamlToJson(constraintRequestBodyAsYaml);
		System.setOut(originalOut);
		System.out.print(result);
	}
	
}
