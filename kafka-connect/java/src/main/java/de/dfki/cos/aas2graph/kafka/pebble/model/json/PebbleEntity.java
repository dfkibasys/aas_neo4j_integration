package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.Entity;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEntity;

import de.dfki.cos.aas2graph.kafka.pebble.model.AssetReferenceInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfos;

public class PebbleEntity extends DefaultEntity implements PebbleSubmodelElement {
	
	@Override
	public void applyLabels(List<String> labels) {
		PebbleSubmodelElement.super.applyLabels(labels);
		labels.add(Entity.class.getSimpleName());
	}
	
	@Override
	public void applyRefs(List<ReferenceInfo> refs) {
		PebbleSubmodelElement.super.applyRefs(refs);
		addSpecificAssetIds(refs);
		addGlobalAssetId(refs);
		
	}

	private void addGlobalAssetId(List<ReferenceInfo> refs) {
		String globalAssetId = getGlobalAssetId();
		if (globalAssetId != null) {
			ReferenceInfo info = new AssetReferenceInfo(globalAssetId);
			refs.add(info);
		}
	}

	private void addSpecificAssetIds(List<ReferenceInfo> refs) {
		for (SpecificAssetId eachId :  getSpecificAssetIds()) {
			Optional.ofNullable(eachId.getExternalSubjectId()).map(ReferenceInfos.toReferenceInfo("externalSubjectId")).ifPresent(refs::add);
			Optional.ofNullable(eachId.getSemanticId()).map(ReferenceInfos.toReferenceInfo("semanticId")).ifPresent(refs::add);
			ReferenceInfos.addAll(eachId.getSupplementalSemanticIds(), refs, "supplementalSemanticIds");
		}
	}

	@Override
	public List<SubmodelElement> getChildren() {
		return getStatements();
	}
}
