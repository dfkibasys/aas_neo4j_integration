package de.dfki.cos.aas2graph.kafka.pebble;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.FileLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;

public class HttpRequestBodyGenerator {

	private final PebbleEngine engine;

	public HttpRequestBodyGenerator() {
		FileLoader loader = new FileLoader();
		loader.setPrefix(locationPrefix());
		engine = new PebbleEngine.Builder().loader(loader).extension(new Neo4jTransformationExtension()).cacheActive(true).build();
		warmup();
	}
	
	private void warmup() {
		
		try {
			for (Path name : Files.list(Path.of(locationPrefix())).collect(Collectors.toList())) {
				engine.getTemplate(name.getName(name.getNameCount()-1).toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		String result = writer.toString();
		return result;
	}

	private String locationPrefix() {
		String location = System.getenv("BASYX_TEMPLATE_LOCATION");
		if (location == null) {
			return System.getProperty("user.dir") + "/templates";
		}
		return location;
	}

}