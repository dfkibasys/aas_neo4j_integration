package org.eclipse.basyx.kafka.connect.neo4j.pebble;

import java.util.Map;

import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Function;

class Neo4jTransformationExtension extends AbstractExtension {

	@Override
	public Map<String, Function> getFunctions() {
		return Map.of("collectAllSubmodelElements", new CollectSubmodelElementsFunction());
	}
}
