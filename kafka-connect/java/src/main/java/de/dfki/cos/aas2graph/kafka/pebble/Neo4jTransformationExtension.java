package de.dfki.cos.aas2graph.kafka.pebble;

import java.util.Map;

import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.extension.Function;

class Neo4jTransformationExtension extends AbstractExtension {

	@Override
	public Map<String, Function> getFunctions() {
		return Map.of("collectAllSubmodelElements", new CollectSubmodelElementsFunction());
	}
	
	@Override
	public Map<String, Filter> getFilters() {
		return Map.of("capitalize", new CapitalizeFilter());
	}
}
