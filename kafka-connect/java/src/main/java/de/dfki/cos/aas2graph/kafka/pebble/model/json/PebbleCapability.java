package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.Capability;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultCapability;

public class PebbleCapability extends DefaultCapability implements PebbleSubmodelElement {

	@Override
	public void applyLabels(List<String> labels) {
		PebbleSubmodelElement.super.applyLabels(labels);
		labels.add(Capability.class.getSimpleName());
	}
	
	@Override
	public List<SubmodelElement> getChildren() {
		return List.of();
	}

	
}
