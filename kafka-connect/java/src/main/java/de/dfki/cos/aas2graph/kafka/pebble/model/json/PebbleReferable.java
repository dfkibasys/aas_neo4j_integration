package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.Extension;
import org.eclipse.digitaltwin.aas4j.v3.model.Referable;

import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfos;

public interface PebbleReferable extends PebbleModel, Referable {

	@Override
	default void applyLabels(List<String> labels) {
		labels.add(Referable.class.getSimpleName());
	}
	
	@Override
	default void applyRefs(List<ReferenceInfo> refs) {
		addExtensions(refs);
	}	
	
	private void addExtensions(List<ReferenceInfo> refs) {		
		for (Extension eachExtension : getExtensions()) {
			addExtension(eachExtension, refs);
		}
	}

	private void addExtension(Extension extension, List<ReferenceInfo> refs) {
		ReferenceInfos.addAll(extension.getRefersTo(), refs, "refersTo");
		Optional.of(extension).map(Extension::getSemanticId).map(ReferenceInfos.toReferenceInfo("semanticId")).ifPresent(refs::add);
		ReferenceInfos.addAll(extension.getSupplementalSemanticIds(), refs, "supplementalSemanticIds");
	}
}
