package org.eclipse.basyx.kafka.connect.neo4j.pebble;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.SubmodelElementInfo;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.SubmodelInfo;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;

import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

class CollectSubmodelElementsFunction implements Function {

	public static final String SUBMODEL_ARG = "sm";

	@Override
	public List<String> getArgumentNames() {
		return List.of(SUBMODEL_ARG);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
		Map<String, Object> arg = (Map<String, Object>) args.get(SUBMODEL_ARG);
		SubmodelInfo submodel = new SubmodelInfo(arg);
		String smId = submodel.getId();
		List<SubmodelElementInfo> elems = submodel.getSubmodelElements();
		return collectElements(smId, elems);
	}

	private List<SubmodelElementInfo> collectElements(String smId, List<SubmodelElementInfo> elems) {
		LinkedList<SubmodelElementInfo> toReturn = new LinkedList<>();
		LinkedList<SubmodelElementInfo> toProcess = new LinkedList<>(elems);
		for (SubmodelElementInfo eachElem : toProcess) {
			eachElem.setIdShortPath(eachElem.getIdShort());
		}
		while (!toProcess.isEmpty()) {
			SubmodelElementInfo inputElem = toProcess.removeFirst();
			toReturn.add(inputElem);
			toProcess.addAll(getChildren(inputElem, toReturn.size() - 1));
		}
		toReturn.stream().map(SubmodelElementInfo::getIdShortPath).forEach(System.out::println);
		return toReturn;
	}

	private List<SubmodelElementInfo> getChildren(SubmodelElementInfo eachElem, int parentPos) {
		List<SubmodelElementInfo> children = eachElem.getChildren();
		String parentIdShortPath = eachElem.getIdShortPath();
		if ("SubmodelElementList".equals(eachElem.getModelType())) {
			int index = 0;
			for (SubmodelElementInfo eachChild : children) {
				eachChild.setIdShortPath(parentIdShortPath + "[" + index + "]");
				eachChild.setParentPos(parentPos);
				index++;
			}
		} else {
			for (SubmodelElementInfo eachChild : children) {
				eachChild.setIdShortPath(parentIdShortPath + "." + eachChild.getIdShort());
				eachChild.setParentPos(parentPos);
			}
		}
		return children;
	}

}
