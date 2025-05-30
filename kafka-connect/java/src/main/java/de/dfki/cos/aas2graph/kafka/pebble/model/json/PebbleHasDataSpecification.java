package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.HasDataSpecification;

import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfos;

public interface PebbleHasDataSpecification extends HasDataSpecification, PebbleModel {

	@Override
	default void applyRefs(List<ReferenceInfo> refs) {
		for (EmbeddedDataSpecification eachSpec : getEmbeddedDataSpecifications()) {
			Optional.ofNullable(eachSpec.getDataSpecification()).map(ReferenceInfos.toReferenceInfo("dataSpecification")).ifPresent(refs::add);
		}
	}
}
