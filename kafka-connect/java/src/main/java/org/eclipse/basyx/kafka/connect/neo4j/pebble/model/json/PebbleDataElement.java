package org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.DataElement;

public interface PebbleDataElement extends DataElement, PebbleSubmodelElement {

	
	@Override
	default void applyLabels(List<String> labels) {
		PebbleSubmodelElement.super.applyLabels(labels);
		labels.add(DataElement.class.getSimpleName());
	}
}
