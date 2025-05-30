package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.BasicEventElement;
import org.eclipse.digitaltwin.aas4j.v3.model.EventElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultBasicEventElement;

import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfos;

public class PebbleBasicEventElement extends DefaultBasicEventElement implements PebbleSubmodelElement {

	@Override
	public void applyRefs(List<ReferenceInfo> refs) {
		PebbleSubmodelElement.super.applyRefs(refs);
		Optional.ofNullable(getMessageBroker()).map(ReferenceInfos.toReferenceInfo("messageBroker")).ifPresent(refs::add);
		Optional.ofNullable(getObserved()).map(ReferenceInfos.toReferenceInfo("observed")).ifPresent(refs::add);
	}

	@Override
	public void applyLabels(List<String> labels) {
		PebbleSubmodelElement.super.applyLabels(labels);
		labels.add(BasicEventElement.class.getSimpleName());
		labels.add(EventElement.class.getSimpleName());
	}
	
	@Override
	public List<SubmodelElement> getChildren() {
		return List.of();
	}
	
}
