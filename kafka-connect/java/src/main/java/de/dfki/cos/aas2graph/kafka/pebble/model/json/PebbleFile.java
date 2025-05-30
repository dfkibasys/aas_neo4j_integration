package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultFile;

public class PebbleFile extends DefaultFile implements PebbleDataElement {

	@Override
	public void applyLabels(List<String> labels) {
		PebbleDataElement.super.applyLabels(labels);
		labels.add(File.class.getSimpleName());
	}

	@Override
	public List<SubmodelElement> getChildren() {
		return List.of();
	}

}
