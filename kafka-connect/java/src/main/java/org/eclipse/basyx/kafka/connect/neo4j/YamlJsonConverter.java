package org.eclipse.basyx.kafka.connect.neo4j;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YamlJsonConverter {
 
	private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory()); 
	private final ObjectMapper jsonMapper = new ObjectMapper();

	@SuppressWarnings("unchecked")
	public String yamlToJson(String yaml) throws JsonMappingException, JsonProcessingException {
		Map<String, Object> result = yamlMapper.readValue(yaml, Map.class);
		return writeMapAsJsonString(result);
	}
	
	public ObjectMapper getJsonMapper() {
		return jsonMapper;
	}

	public String writeMapAsJsonString(Map<String, Object> content) throws JsonProcessingException {
		 return jsonMapper.writeValueAsString(content);
	}
}
