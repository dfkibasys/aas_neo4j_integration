package org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json;

import java.util.List;
import java.util.Optional;

import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.ReferenceInfo;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.ReferenceInfos;
import org.eclipse.digitaltwin.aas4j.v3.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.HasDataSpecification;

public interface PebbleHasDataSpecification extends HasDataSpecification, PebbleModel {

	@Override
	default void applyRefs(List<ReferenceInfo> refs) {
		for (EmbeddedDataSpecification eachSpec : getEmbeddedDataSpecifications()) {
			Optional.ofNullable(eachSpec.getDataSpecification()).map(ReferenceInfos.toReferenceInfo("dataSpecification")).ifPresent(refs::add);
		}
	}
}
