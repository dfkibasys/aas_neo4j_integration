package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;

import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfo;

public interface PebbleSubmodelElement extends SubmodelElement, PebbleHasSemantics, PebbleReferable, PebbleHasDataSpecification, PebbleQualifiable {
	
	List<SubmodelElement> getChildren();
	
	
	@Override
	default void applyLabels(List<String> labels) {
		PebbleReferable.super.applyLabels(labels);
		labels.add(SubmodelElement.class.getSimpleName());
	}
	
	@Override
	default void applyRefs(List<ReferenceInfo> refs) {
		PebbleReferable.super.applyRefs(refs);
		PebbleHasSemantics.super.applyRefs(refs);
		PebbleHasDataSpecification.super.applyRefs(refs);
		PebbleQualifiable.super.applyRefs(refs);
	}
	
}
