package org.eclipse.basyx.kafka.connect.neo4j.util;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class SerializationTools {

	private final JsonDeserializer jsonDeserializer = new JsonDeserializer();

	private final JsonSerializer jsonSerializer = new JsonSerializer();

	private final ObjectMapper jsonMapper = new ObjectMapper().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

	private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory()).configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

	public JsonDeserializer jsonDeserializer() {
		return jsonDeserializer;
	}

	public ObjectMapper jsonMapper() {
		return jsonMapper;
	}

	public JsonSerializer jsonSerializer() {
		return jsonSerializer;
	}

	public ObjectMapper yamlMapper() {
		return yamlMapper;
	}
}
