package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.HasSemantics;

import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfos;

public interface PebbleHasSemantics extends HasSemantics, PebbleModel {

	@Override
	default void applyRefs(List<ReferenceInfo> refs) {
		Optional.ofNullable(this.getSemanticId()).map(ReferenceInfos.toReferenceInfo("semanticId")).ifPresent(refs::add);
		ReferenceInfos.addAll(this.getSupplementalSemanticIds(), refs, "supplementalSemanticIds");
	}
}
