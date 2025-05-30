package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;

public class PebbleSubmodelElementCollection extends DefaultSubmodelElementCollection implements PebbleSubmodelElement {

	@Override
	public void applyLabels(List<String> labels) {
		PebbleSubmodelElement.super.applyLabels(labels);
		labels.add(SubmodelElementCollection.class.getSimpleName());
	}
	
	@Override
	public List<SubmodelElement> getChildren() {
		return getValue();
	}

}
