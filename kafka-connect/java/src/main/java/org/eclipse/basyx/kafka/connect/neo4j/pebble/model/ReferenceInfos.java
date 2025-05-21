package org.eclipse.basyx.kafka.connect.neo4j.pebble.model;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;

public class ReferenceInfos {

	private ReferenceInfos() {

	}

	public static Function<Reference, ReferenceInfo> toReferenceInfo(String refType) {
		return r -> toReferenceInfo(r, refType);
	}

	public static ReferenceInfo toReferenceInfo(Reference ref, String refType) {
		List<Key> allKeys = ref.getKeys();
		if (allKeys.size() == 0) {
			return null;
		} else if (allKeys.size() == 1) {
			return resolve(allKeys.get(0), refType);
		} else {
			return resolveKeyChain(allKeys, refType);
		}
	}

	private static ReferenceInfo resolveKeyChain(List<Key> allKeys, String refType) {
		Key firstKey = allKeys.get(0);
		if (firstKey.getType() != KeyTypes.SUBMODEL) {
			return null;
		}

		Key lastKey = allKeys.get(allKeys.size() - 1);
		String submodelId = firstKey.getValue();
		String smePath = buildPath(allKeys.subList(1, allKeys.size()));
		return new SubmodelElementReferenceInfo(lastKey.getType(), submodelId, smePath, refType);
	}

	private static String buildPath(List<Key> keys) {
		StringBuilder pathBuilder = new StringBuilder();
		boolean isFirstElement = true;
		boolean onList = false;
		for (Key eachKey : keys.subList(1, keys.size())) {
			if (onList) {
				pathBuilder.append('[');
			} else if (!isFirstElement) {
				pathBuilder.append('.');
			}
			// for now we expect that for list elements we do have a position as value
			String value = eachKey.getValue();
			pathBuilder.append(value);
			if (onList) {
				pathBuilder.append(']');
			}
			if (eachKey.getType() == KeyTypes.SUBMODEL_ELEMENT_LIST) {
				onList = true;
			} else {
				onList = false;
			}
			isFirstElement = false;
		}
		return pathBuilder.toString();
	}

	private static ReferenceInfo resolve(Key eachKey, String refType) {
		KeyTypes type = eachKey.getType();
		String value = eachKey.getValue();
		switch (eachKey.getType()) {
		case ASSET_ADMINISTRATION_SHELL:
		case SUBMODEL:
		case CONCEPT_DESCRIPTION:
			return new IdentifiableReferenceInfo(type, value, refType);
		default:
			// a submodel element should have at least 2 key elements
			return null;
		}
	}

	public static void addAll(List<Reference> refs, List<ReferenceInfo> toAdd, String refType) {
		if (refs == null) {
			return;
		}
		for (Reference eachRef : refs) {
			ReferenceInfo info = ReferenceInfos.toReferenceInfo(eachRef, refType);
			if (info != null) {
				toAdd.add(info);
			}
		}

	}

	public static Comparator<? super ReferenceInfo> comparator() {
		return Comparator.comparing(ReferenceInfo::getRefType).thenComparing(Comparator.comparing(ReferenceInfo::getLabel));
	}

	public static List<ReferenceInfo> sort(List<ReferenceInfo> refs) {
		refs.sort(ReferenceInfos.comparator());
		return refs;
	}

}
