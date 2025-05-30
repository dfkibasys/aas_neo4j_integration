package de.dfki.cos.aas2graph.kafka.init;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import de.dfki.cos.aas2graph.kafka.pebble.HttpRequestBodyGenerator;
import de.dfki.cos.aas2graph.kafka.util.YamlJsonConverter;

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
