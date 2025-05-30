package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReferenceElement;

import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfos;

public class PebbleReferenceElement extends DefaultReferenceElement implements PebbleDataElement {

	@Override
	public void applyLabels(List<String> labels) {
		PebbleDataElement.super.applyLabels(labels);
		labels.add(ReferenceElement.class.getSimpleName());
	}
	
	@Override
	public void applyRefs(List<ReferenceInfo> refs) {
		PebbleDataElement.super.applyRefs(refs);
		Optional.ofNullable(getValue()).map(ReferenceInfos.toReferenceInfo("value")).ifPresent(refs::add);
	}
	
	@Override
	public List<SubmodelElement> getChildren() {
		return List.of();
	}	
	
}
