package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AnnotatedRelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.RelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAnnotatedRelationshipElement;

import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfo;

public class PebbleAnnotatedRelationshipElement extends DefaultAnnotatedRelationshipElement implements PebbleSubmodelElement {

	@Override
	public List<SubmodelElement> getChildren() {
		return new ArrayList<>(getAnnotations());
	}
	
	@Override
	public void applyRefs(List<ReferenceInfo> refs) {
		PebbleSubmodelElement.super.applyRefs(refs);
		RelationshipElementRefs.addRelationshipReferences(this, refs);
	}

	@Override
	public void applyLabels(List<String> labels) {
		PebbleSubmodelElement.super.applyLabels(labels);
		labels.add(AnnotatedRelationshipElement.class.getSimpleName());
		labels.add(RelationshipElement.class.getSimpleName());
	}
}
