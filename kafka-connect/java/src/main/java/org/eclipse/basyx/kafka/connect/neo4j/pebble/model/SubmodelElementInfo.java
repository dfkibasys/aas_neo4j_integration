package org.eclipse.basyx.kafka.connect.neo4j.pebble.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SubmodelElementInfo {

	private final Map<String, Object> elem;
	private Integer parentPos;
	private String idShortPath;

	public SubmodelElementInfo(Map<String, Object> elem) {
		this.elem = elem;
	}

	public String getIdShort() {
		return (String) elem.get("idShort");
	}

	public void setIdShortPath(String path) {
		this.idShortPath = path;
	}

	public Map<String, Object> asMap() {
		return elem;
	}

	public String getModelType() {
		return (String) elem.get("modelType");
	}

	@SuppressWarnings("unchecked")
	public List<SubmodelElementInfo> getChildren() {
		String modelType = getModelType();
		if ("SubmodelElementCollection".equals(modelType) || "SubmodelElementList".equals(modelType)) {
			List<Map<String, Object>> children = (List<Map<String, Object>>) elem.get("value");
			if (children != null) {
				return children.stream().map(SubmodelElementInfo::new).collect(Collectors.toList());
			}
		}
		return List.of();
	}

	public String getValue() {
		String modelType = getModelType();
		if ("Property".equals(modelType)) {
			return (String) elem.get("value");
		}
		return null;
	}

	public String getIdShortPath() {
		return idShortPath;
	}

	public boolean getIsRootElement() {
		return parentPos == null;
	}

	public void setParentPos(Integer parentPos) {
		this.parentPos = parentPos;
	}

	public Integer getParentPos() {
		return parentPos;
	}

	@Override
	public String toString() {
		return "SubmodelElementInfo [idShort: " + getIdShort() + ", modelType: " + getModelType() + ", value: "
				+ getValue() + ", idShortPath:" + getIdShortPath() + ", parentPos:" + getParentPos() + ", children: "
				+ getChildren() + "]";
	}

}
