package org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json;

import java.util.List;
import java.util.Optional;

import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.ReferenceInfo;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.ReferenceInfos;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;

public class PebbleProperty extends DefaultProperty implements PebbleDataElement {

	@Override
	public void applyLabels(List<String> labels) {
		PebbleDataElement.super.applyLabels(labels);
		labels.add(Property.class.getSimpleName());
	}

	@Override
	public void applyRefs(List<ReferenceInfo> refs) {
		PebbleDataElement.super.applyRefs(refs);
		addValueId(refs);
	}

	private void addValueId(List<ReferenceInfo> refs) {
		Optional.ofNullable(getValueId()).map(ReferenceInfos.toReferenceInfo("valueId")).ifPresent(refs::add);
	}

	@Override
	public List<SubmodelElement> getChildren() {
		return List.of();
	}

}
