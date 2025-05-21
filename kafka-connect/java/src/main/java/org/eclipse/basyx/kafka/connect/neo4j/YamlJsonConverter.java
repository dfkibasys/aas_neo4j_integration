package org.eclipse.basyx.kafka.connect.neo4j;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class YamlJsonConverter {
 

	@SuppressWarnings("unchecked")
	public String yamlToJson(String yaml) throws JsonMappingException, JsonProcessingException {
		Map<String, Object> result = AasIo.yamlMapper().readValue(yaml, Map.class);
		return writeMapAsJsonString(result);
	}
	

	public String writeMapAsJsonString(Map<String, Object> content) throws JsonProcessingException {
		 return AasIo.jsonMapper().writeValueAsString(content);
	}
}
