package de.dfki.cos.aas2graph.kafka.pebble.model;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;

import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleSubmodelElement;

public class SubmodelElementInfo extends ParentInfo {

	private ParentInfo parent;
	private Integer parentPos;
	private final PebbleSubmodelElement element;
	private List<SubmodelElementInfo> children;

	public SubmodelElementInfo(SubmodelElement element) {
		this.element = (PebbleSubmodelElement) element;
	}

	public SubmodelElement getElement() {
		return element;
	}
	
	public String getIdShort() {
		return element.getIdShort();
	}
	
	public List<ReferenceInfo> getRefs() {
		return element.getRefs();
	}

	
	public List<SubmodelElementInfo> getChildren() {
		if (children == null) {
			children = element.getChildren().stream().map(SubmodelElementInfo::new).collect(Collectors.toList());
		}
		return children;
	}

	public String getValue() {
		if (element instanceof Property) {
			Property prop = (Property)element;
			return prop.getValue();
		}
		return null;
	}


	public boolean getIsRootElement() {
		return parent == null;
	}
	
	public void setParent(ParentInfo parent, int parentPos) {
		this.parent = parent;
		this.parentPos = parentPos;
	}

	public Integer getParentPos() {
		return parentPos;
	}
	
	public ParentInfo getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return "SubmodelElementInfo [idShort: " + getIdShort() + ", modelType: " + getModelType() + ", value: "
				+ getValue() + ", idShortPath:" + getIdShortPath() + ", parentPos:" + getParentPos() + ", children: "
				+ getChildren() + "]";
	}

	private String getModelType() {
		return element.getClass().getSuperclass().getInterfaces()[0].getSimpleName();
	}

	public List<String> getLabels() {
		return element.getLabels();
	}


}
