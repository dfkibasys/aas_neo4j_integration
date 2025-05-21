package org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json;

import java.util.List;
import java.util.Optional;

import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.ReferenceInfo;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.ReferenceInfos;
import org.eclipse.digitaltwin.aas4j.v3.model.HasSemantics;

public interface PebbleHasSemantics extends HasSemantics, PebbleModel {

	@Override
	default void applyRefs(List<ReferenceInfo> refs) {
		Optional.ofNullable(this.getSemanticId()).map(ReferenceInfos.toReferenceInfo("semanticId")).ifPresent(refs::add);
		ReferenceInfos.addAll(this.getSupplementalSemanticIds(), refs, "supplementalSemanticIds");
	}
}
