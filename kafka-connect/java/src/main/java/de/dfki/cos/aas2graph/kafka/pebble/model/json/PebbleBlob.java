package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.Blob;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultBlob;

public class PebbleBlob extends DefaultBlob implements PebbleDataElement {

	@Override
	public void applyLabels(List<String> labels) {
		PebbleDataElement.super.applyLabels(labels);
		labels.add(Blob.class.getSimpleName());
	}
	
	@Override
	public List<SubmodelElement> getChildren() {
		return List.of(); 
	}

}
