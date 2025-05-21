package org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json;

import java.util.List;
import java.util.Optional;

import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.ReferenceInfo;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.ReferenceInfos;
import org.eclipse.digitaltwin.aas4j.v3.model.RelationshipElement;

public class RelationshipElementRefs {

	private RelationshipElementRefs() {
		
	}
	
	public static void addRelationshipReferences(RelationshipElement elem, List<ReferenceInfo> refs) {
		Optional.ofNullable(elem.getFirst()).map(ReferenceInfos.toReferenceInfo("first")).ifPresent(refs::add);
		Optional.ofNullable(elem.getSecond()).map(ReferenceInfos.toReferenceInfo("second")).ifPresent(refs::add);
	}	
}
