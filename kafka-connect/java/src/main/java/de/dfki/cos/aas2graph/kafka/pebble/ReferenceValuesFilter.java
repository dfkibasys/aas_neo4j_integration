package de.dfki.cos.aas2graph.kafka.pebble;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

public class ReferenceValuesFilter implements Filter {

	@Override
	public List<String> getArgumentNames() {
		return null;
	}

	@Override
	public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context,
			int lineNumber) throws PebbleException {
		if (input instanceof Reference) {
			return resolveReferenceValues((Reference) input);
		}
		return null;

	}

	private List<String> resolveReferenceValues(Reference ref) {
		List<Key> keys = ref.getKeys();
		if (keys == null) {
			return null;
		}
		List<String> keyValues = new ArrayList<>(keys.size());
		for (Key eachKey : keys) {
			String value = eachKey.getValue();
			if (value != null) {
				keyValues.add(value);
			}
		}
		return keyValues;
	}

}
