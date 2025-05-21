package org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.Range;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultRange;

public class PebbleRange extends DefaultRange implements PebbleDataElement {

	@Override
	public List<SubmodelElement> getChildren() {
		return List.of();
	}

	@Override
	public void applyLabels(List<String> labels) {
		PebbleDataElement.super.applyLabels(labels);
		labels.add(Range.class.getSimpleName());
	}

}
