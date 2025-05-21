package org.eclipse.basyx.kafka.connect.neo4j.pebble;

import java.util.List;
import java.util.Map;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

public class CapitalizeFilter implements Filter {

	@Override
	public List<String> getArgumentNames() {
		return null;
	}
	
	@Override
	public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) throws PebbleException {
	    if (input == null) {
	    	return null;
	    }
        String str = input.toString();
        if (str.isEmpty()) {
        	return str;
        } else if (str.length() == 1) {
        	return str.toUpperCase();
        }
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }

}
