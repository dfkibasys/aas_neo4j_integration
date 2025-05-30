package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.Identifiable;
import org.eclipse.digitaltwin.aas4j.v3.model.Referable;

public interface PebbleIdentifiable extends PebbleReferable, Referable {

	@Override
	default void applyLabels(List<String> labels) {
		PebbleReferable.super.applyLabels(labels);
		labels.add(Identifiable.class.getSimpleName());
	}
	
}
