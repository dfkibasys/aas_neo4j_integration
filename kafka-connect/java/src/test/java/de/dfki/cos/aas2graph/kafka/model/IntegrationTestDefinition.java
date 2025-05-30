package de.dfki.cos.aas2graph.kafka.model;

import java.util.List;

import de.dfki.cos.aas2graph.kafka.model.operations.IntegrationTestOperation;

public class IntegrationTestDefinition {

	private List<IntegrationTestOperation<?>> input;

	private IntegrationTestResult expected;

	public IntegrationTestResult getExpected() {
		return expected;
	}

	public void setExpected(IntegrationTestResult expected) {
		this.expected = expected;
	}

	public List<IntegrationTestOperation<?>> getInput() {
		return input;
	}

	public void setInput(List<IntegrationTestOperation<?>> input) {
		this.input = input;
	}
}