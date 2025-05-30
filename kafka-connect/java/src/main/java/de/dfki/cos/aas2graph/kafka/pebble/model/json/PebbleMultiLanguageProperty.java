package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.MultiLanguageProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultMultiLanguageProperty;

import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfos;

public class PebbleMultiLanguageProperty extends DefaultMultiLanguageProperty implements PebbleDataElement {

	@Override
	public void applyLabels(List<String> labels) {
		PebbleDataElement.super.applyLabels(labels);
		labels.add(MultiLanguageProperty.class.getSimpleName());
	}

	@Override
	public void applyRefs(List<ReferenceInfo> refs) {
		PebbleDataElement.super.applyRefs(refs);
		Optional.ofNullable(getValueId()).map(ReferenceInfos.toReferenceInfo("valueId")).ifPresent(refs::add);
	}

	@Override
	public List<SubmodelElement> getChildren() {
		return List.of();
	}
}
