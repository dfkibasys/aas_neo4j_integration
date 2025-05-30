package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.RelationshipElement;

import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfos;

public class RelationshipElementRefs {

	private RelationshipElementRefs() {
		
	}
	
	public static void addRelationshipReferences(RelationshipElement elem, List<ReferenceInfo> refs) {
		Optional.ofNullable(elem.getFirst()).map(ReferenceInfos.toReferenceInfo("first")).ifPresent(refs::add);
		Optional.ofNullable(elem.getSecond()).map(ReferenceInfos.toReferenceInfo("second")).ifPresent(refs::add);
	}	
}
