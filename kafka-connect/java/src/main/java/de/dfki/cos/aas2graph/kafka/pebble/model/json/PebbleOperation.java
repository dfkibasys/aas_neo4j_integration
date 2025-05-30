package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;

public class PebbleOperation extends DefaultOperation implements PebbleSubmodelElement{

	@Override
	public void applyLabels(List<String> labels) {
		PebbleSubmodelElement.super.applyLabels(labels);
		labels.add(Operation.class.getSimpleName());
	}
	
	@Override
	public List<SubmodelElement> getChildren() {
		List<SubmodelElement> elements = new ArrayList<>();
		addOperationVariables(getInputVariables(), elements);
		addOperationVariables(getInoutputVariables(), elements);
		addOperationVariables(getOutputVariables(), elements);
		return elements;
	}
	
	private void addOperationVariables(List<OperationVariable> vars, List<SubmodelElement> elements) {
		for (OperationVariable elem : getInputVariables()) {
			Optional.ofNullable(elem.getValue()).ifPresent(elements::add);
		}
	}

	
	
}
