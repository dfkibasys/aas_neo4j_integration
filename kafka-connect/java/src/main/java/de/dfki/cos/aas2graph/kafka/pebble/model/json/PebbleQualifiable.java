package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.Qualifiable;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;

import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfos;

public interface PebbleQualifiable extends PebbleModel, Qualifiable {

	@Override
	default void applyRefs(List<ReferenceInfo> refs) {
		for (Qualifier eachQualifier : getQualifiers()) {
			Optional.ofNullable(eachQualifier.getSemanticId()).map(ReferenceInfos.toReferenceInfo("semanticId")).ifPresent(refs::add);
			ReferenceInfos.addAll(eachQualifier.getSupplementalSemanticIds(), refs, "supplementalSemanticids");
		}
	}
}
