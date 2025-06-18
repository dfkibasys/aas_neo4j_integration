package de.dfki.cos.aas2graph.kafka.pebble;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;

import de.dfki.cos.aas2graph.kafka.pebble.model.ParentInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.SubmodelElementInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleSubmodel;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleSubmodelElement;
import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

class CollectSubmodelElementsFunction implements Function {

	public static final String VALUE_ARG = "value";

	public static final String ID_ARG = "id";

	public static final String IDSHORTPATH_ARG = "idShortPath";

	public List<String> getArgumentNames() {
		return List.of(VALUE_ARG, ID_ARG, IDSHORTPATH_ARG);
	}

	@Override
	public List<SubmodelElementInfo> execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
		Object obj = args.get(VALUE_ARG);
		if (obj instanceof Submodel) {
			return collectSubmodelElements((PebbleSubmodel) obj);
		} else if (obj instanceof SubmodelElement) {
			String smId = (String) args.get(ID_ARG);
			String idShortPath = (String) args.get(IDSHORTPATH_ARG);
			return collectSubmodelElementElements((PebbleSubmodelElement) obj, smId, idShortPath);
		}
		throw new RuntimeException();

	}

	private List<SubmodelElementInfo> collectSubmodelElementElements(PebbleSubmodelElement elem, String smId, String idShortPath) {
		// including the current element
		SubmodelElementInfo info = new SubmodelElementInfo(elem);
		info.setIdShortPath(idShortPath);
		setParentOrIsRoot(info, smId, idShortPath);
		return collectElements(smId, List.of(info));
	}

	private List<SubmodelElementInfo> collectSubmodelElements(PebbleSubmodel submodel) {
		String smId = submodel.getId();
		List<SubmodelElementInfo> elems = submodel.getSubmodelElements().stream().map(SubmodelElementInfo::new).map(e -> {
			e.setIdShortPath(e.getIdShort());
			return e;
		}).collect(Collectors.toList());
		return collectElements(smId, elems);
	}

	private void setParentOrIsRoot(SubmodelElementInfo info, String smId, String idShortPath) {
		// If no path is provided, this is a root element
		if (idShortPath == null || idShortPath.isEmpty()) {
			return;
		}

		int lastDot = idShortPath.lastIndexOf('.');
		int lastBracketOpen = idShortPath.lastIndexOf('[');
		int lastBracketClose = idShortPath.lastIndexOf(']');

		String parentPath;
		Integer parentPos = null;

		// Case: ends with index (e.g. a.b[3])
		if (lastBracketOpen != -1 && lastBracketClose > lastBracketOpen && lastBracketClose == idShortPath.length() - 1) {
			// Extract parent path and index
			parentPath = idShortPath.substring(0, lastBracketOpen);
			String indexStr = idShortPath.substring(lastBracketOpen + 1, lastBracketClose);
			try {
				parentPos = Integer.parseInt(indexStr);
			} catch (NumberFormatException e) {

			}
		}
		// Case: dot-separated path (e.g. a.b.c)
		else if (lastDot != -1) {
			parentPath = idShortPath.substring(0, lastDot);
		}
		// Case: single-level list (e.g. list[2])
		else if (lastBracketOpen != -1 && lastBracketClose > lastBracketOpen && lastBracketClose == idShortPath.length() - 1) {
			parentPath = idShortPath.substring(0, lastBracketOpen);
			String indexStr = idShortPath.substring(lastBracketOpen + 1, lastBracketClose);
			try {
				parentPos = Integer.parseInt(indexStr);
			} catch (NumberFormatException e) {
			}
		}
		// No parent
		else {
			return;
		}

		// Mark as non-root and assign parent reference path
		ParentInfo parent = new ParentInfo();
		parent.setIdShortPath(parentPath);
		info.setParent(parent, parentPos == null ? -1 : parentPos);

	}

	private List<SubmodelElementInfo> collectElements(String smId, List<SubmodelElementInfo> elems) {
		LinkedList<SubmodelElementInfo> toReturn = new LinkedList<>();
		LinkedList<SubmodelElementInfo> toProcess = new LinkedList<>(elems);
		for (SubmodelElementInfo eachElem : toProcess) {
			eachElem.setIdShortPath(eachElem.getIdShortPath());
		}
		while (!toProcess.isEmpty()) {
			SubmodelElementInfo inputElem = toProcess.removeFirst();
			toReturn.add(inputElem);
			int parentPos = toReturn.size() - 1;
			toProcess.addAll(getChildren(inputElem, toReturn.get(parentPos), parentPos));
		}
		return toReturn;
	}

	private List<SubmodelElementInfo> getChildren(SubmodelElementInfo eachElem, ParentInfo parent, int parentPos) {
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
