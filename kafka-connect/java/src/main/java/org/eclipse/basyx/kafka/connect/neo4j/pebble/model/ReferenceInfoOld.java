package org.eclipse.basyx.kafka.connect.neo4j.pebble.model;

public class ReferenceInfoOld {


//	private final String id;

//	private ReferenceInfoOld(KeyTypes targetType, String id, String idShortPath, String refType) {
//		this.id = id;
//		this.idShortPath = idShortPath;
//		this.targetType = targetType;
//		this.refType = refType;
//	}
//
//
//	public static ReferenceInfoOld forSubmodelElement(KeyTypes type, String submodelId, String smePath, String refType) {
//		if (submodelId == null) {
//			return null;
//		}
//		return new ReferenceInfoOld(type, submodelId, smePath, refType);
//	}
//
//	public static ReferenceInfoOld forIdentifiable(KeyTypes type, String id, String refType) {
//		if (id == null) {
//			return null;
//		}
//		return new ReferenceInfoOld(type, id, null, refType);
//	}
//
//


//
//	public boolean isShell() {
//		return targetType == KeyTypes.ASSET_ADMINISTRATION_SHELL;
//	}
//
//	public boolean isSubmodel() {
//		return targetType == KeyTypes.SUBMODEL;
//	}
//
//	private boolean isConceptDescription() {
//		return targetType == KeyTypes.CONCEPT_DESCRIPTION;
//	}

//	public boolean isIdentifiable() {
//		return isShell() || isSubmodel() || isConceptDescription() || KeyTypes.IDENTIFIABLE == targetType;
//	}

//	public String getRefType() {
//		return refType;
//	}

//	public boolean isSubmodelElement() {
//		return !isIdentifiable();
//	}
//
//	@Override
//	public String toString() {
//		return "ReferenceInfo [reftype=" + refType + "targettype=" + targetType + ", smePath=" + idShortPath + ", id=" + id + "]";
//	}


}
