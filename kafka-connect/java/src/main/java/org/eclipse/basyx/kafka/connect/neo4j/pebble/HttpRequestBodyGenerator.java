package org.eclipse.basyx.kafka.connect.neo4j.pebble;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.FileLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;

public class HttpRequestBodyGenerator {

	private final PebbleEngine engine;

	public HttpRequestBodyGenerator() {
		FileLoader loader = new FileLoader();
		loader.setPrefix(locationPrefix());
		engine = new PebbleEngine.Builder().loader(loader).extension(new Neo4jTransformationExtension()).build();
	}
	
	public String generateYamlFromMessage(PebbleContext context) throws IOException {
		return generateYaml("event-cypher-request.peb", context.toMap());
	}

	
	
	public String generateYamlForInitialCypherRequests() throws IOException {
		return generateYaml("initial-cypher-request.peb", Map.of());
	}
	
	private String generateYaml(String templateName, Map<String, Object> contextAsMap) throws IOException {
		PebbleTemplate compiledTemplate = engine.getTemplate(templateName);
		StringWriter writer = new StringWriter();
		compiledTemplate.evaluate(writer, contextAsMap);
		return writer.toString();
	}

	private String locationPrefix() {
		String location = System.getenv("BASYX_TEMPLATE_LOCATION");
		if (location == null) {
			return System.getProperty("user.dir") + "/templates";
		}
		return location;
	}

}