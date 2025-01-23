package org.eclipse.basyx.kafka.connect.neo4j.pebble.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SubmodelInfo {

	private final Map<String, Object> element;
	
	public SubmodelInfo(Map<String, Object> element) {
		this.element = element;
	}

	public String getId() {
		return (String) element.get("id");
	}

	@SuppressWarnings("unchecked")
	public List<SubmodelElementInfo> getSubmodelElements() {
		List<Map<String, Object>> elements = (List<Map<String, Object>>) element.get("submodelElements");
		if (elements == null) {
			return List.of();
		}
		String id = getId();
		return elements.stream().map(SubmodelElementInfo::new).collect(Collectors.toList());
	}
	 
	
	
}
