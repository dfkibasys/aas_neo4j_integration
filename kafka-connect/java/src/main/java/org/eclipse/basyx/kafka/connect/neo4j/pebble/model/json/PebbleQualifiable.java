package org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json;

import java.util.List;
import java.util.Optional;

import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.ReferenceInfo;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.ReferenceInfos;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifiable;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;

public interface PebbleQualifiable extends PebbleModel, Qualifiable {

	@Override
	default void applyRefs(List<ReferenceInfo> refs) {
		for (Qualifier eachQualifier : getQualifiers()) {
			Optional.ofNullable(eachQualifier.getSemanticId()).map(ReferenceInfos.toReferenceInfo("semanticId")).ifPresent(refs::add);
			ReferenceInfos.addAll(eachQualifier.getSupplementalSemanticIds(), refs, "supplementalSemanticids");
		}
	}
}
