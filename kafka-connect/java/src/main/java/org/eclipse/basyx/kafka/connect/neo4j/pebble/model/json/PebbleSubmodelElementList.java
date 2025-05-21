package org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;

public class PebbleSubmodelElementList extends DefaultSubmodelElementList implements PebbleSubmodelElement {
	
	@Override
	public void applyLabels(List<String> labels) {
		PebbleSubmodelElement.super.applyLabels(labels);
		labels.add(SubmodelElementList.class.getSimpleName());
	}
	
	@Override
	public List<SubmodelElement> getChildren() {
		return getValue();
	}

}
