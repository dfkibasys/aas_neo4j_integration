package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.AdministrativeInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;

import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfos;

public class PebbleAssetAdministrationShell extends DefaultAssetAdministrationShell implements PebbleIdentifiable, PebbleHasDataSpecification {

	@Override
	public List<String> getLabels() {
		return PebbleIdentifiable.super.getLabels();
	}
	
	@Override
	public void applyLabels(List<String> labels) {
		PebbleIdentifiable.super.applyLabels(labels);
		labels.add(AssetAdministrationShell.class.getSimpleName());
	}
	
	@Override
	public void applyRefs(List<ReferenceInfo> refs) {
		PebbleIdentifiable.super.applyRefs(refs);
		addAdministrationRefs(this.getAdministration(), refs);
		addAssetInformationRefs(this.getAssetInformation(), refs);
		Optional.ofNullable(this.getDerivedFrom()).map(ReferenceInfos.toReferenceInfo("derivedFrom")).ifPresent(refs::add);
		PebbleHasDataSpecification.super.applyRefs(refs);
	}


	private void addAssetInformationRefs(AssetInformation info, List<ReferenceInfo> refs) {
		if (info == null) {
			return;
		}
		for (SpecificAssetId eachId : info.getSpecificAssetIds()) {
			Optional.ofNullable(eachId.getExternalSubjectId()).map(ReferenceInfos.toReferenceInfo("externalSubjectId")).ifPresent(refs::add);
			Optional.ofNullable(eachId.getSemanticId()).map(ReferenceInfos.toReferenceInfo("semanticId")).ifPresent(refs::add);
			ReferenceInfos.addAll(eachId.getSupplementalSemanticIds(), refs, "supplementalSemanticIds");
		}
	}

	private void addAdministrationRefs(AdministrativeInformation info, List<ReferenceInfo> refs) {
		if (info == null) {
			return;
		}
		for (EmbeddedDataSpecification eachSpec : info.getEmbeddedDataSpecifications()) {
			Optional.ofNullable(eachSpec.getDataSpecification()).map(ReferenceInfos.toReferenceInfo("dataSpecification")).ifPresent(refs::add);
		}
	}

}
