package de.dfki.cos.aas2graph.kafka.util;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Assert;

import de.dfki.cos.aas2graph.kafka.model.IntegrationTestResultNode;

public class UnknownComparing {

	private final String propertyName;
	private final Object replacement;

	public UnknownComparing(String propertyName, Object replacement) {
		this.propertyName = propertyName;
		this.replacement = replacement;
	}

	public void compareAndReset(List<IntegrationTestResultNode> resultNodes,
			List<IntegrationTestResultNode> expectedNodes) {
		Map<String, IntegrationTestResultNode> resultAsMap = resultNodes.stream()
				.collect(Collectors.toMap(IntegrationTestResultNode::getId, Function.identity()));
		for (IntegrationTestResultNode eachExpected : expectedNodes) {
			IntegrationTestResultNode eachResult = resultAsMap.get(eachExpected.getId());
			compareAndReset(eachExpected, eachResult);
		}
	}

	private void compareAndReset(IntegrationTestResultNode eachExpected, IntegrationTestResultNode eachResult) {
		if (eachResult == null) {
			return;
		}
		compareAndReset(eachExpected.getProperties(), eachResult.getProperties());
	}
	private void compareAndReset(Map<String,Object> expectedProps, Map<String, Object> resultProps) {
		if (expectedProps == null || resultProps == null) {
			return;
		}
		Object valueExpected = expectedProps.get(propertyName);
		Object valueResult =resultProps.get(propertyName);
		if (valueResult == null && valueExpected != null) {
			Assert.assertNull("Property '" + propertyName + "' is expected but not set in result.", valueResult);
		} 
		if (valueResult != null && valueExpected == null ) {
			Assert.assertNotNull("Property '" + propertyName + "' is not expected but set in result.", valueResult);
		}
		resetProperties(expectedProps, resultProps);
	}	

	private void resetProperties(Map<String, Object> expectedProps, Map<String, Object> resultProps) {
		if (expectedProps.containsKey(propertyName)) {
			expectedProps.put(propertyName, replacement);
		}
		if (resultProps.containsKey(propertyName)) {
			resultProps.put(propertyName, replacement);
		}
	}

}
