package de.dfki.cos.aas2graph.kafka.pebble;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;

import de.dfki.cos.aas2graph.kafka.pebble.model.SubmodelElementInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleSubmodel;
import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

class CollectSubmodelElementsFunction implements Function {

	public static final String SUBMODEL_ARG = "sm";

	@Override
	public List<String> getArgumentNames() {
		return List.of(SUBMODEL_ARG);
	}

	@Override
	public List<SubmodelElementInfo> execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
		PebbleSubmodel submodel = (PebbleSubmodel) args.get(SUBMODEL_ARG);
		String smId = submodel.getId();
		List<SubmodelElementInfo> elems = submodel.getSubmodelElements().stream().map(SubmodelElementInfo::new).collect(Collectors.toList());
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
			int parentPos = toReturn.size() - 1;
			toProcess.addAll(getChildren(inputElem, toReturn.get(parentPos), parentPos));
		}
		return toReturn;
	}

	private List<SubmodelElementInfo> getChildren(SubmodelElementInfo eachElem, SubmodelElementInfo parent, int parentPos) {
		List<SubmodelElementInfo> children = eachElem.getChildren();
		String parentIdShortPath = eachElem.getIdShortPath();		
		if (eachElem.getElement() instanceof SubmodelElementList) {
			int index = 0;
			for (SubmodelElementInfo eachChild : children) {
				eachChild.setIdShortPath(parentIdShortPath + "[" + index + "]");
				eachChild.setParent(parent, parentPos);
				index++;
			}
		} else {
			for (SubmodelElementInfo eachChild : children) {
				eachChild.setIdShortPath(parentIdShortPath + "." + eachChild.getIdShort());
				eachChild.setParent(parent, parentPos);
			}
		}
		return children;
	}

}
