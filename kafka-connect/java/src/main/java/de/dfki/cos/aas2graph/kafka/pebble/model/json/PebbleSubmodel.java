package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.AdministrativeInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfos;

public class PebbleSubmodel extends DefaultSubmodel implements PebbleModel, PebbleHasSemantics, PebbleIdentifiable, PebbleHasDataSpecification {

	@Override
	public void applyLabels(List<String> labels) {
		labels.add(Submodel.class.getSimpleName());
		PebbleIdentifiable.super.applyLabels(labels);
	}

	@Override
	public void applyRefs(List<ReferenceInfo> refs) {
		PebbleIdentifiable.super.applyRefs(refs);		
		PebbleHasSemantics.super.applyRefs(refs);
		addAdministativeInformation(getAdministration(), refs);
		PebbleHasDataSpecification.super.applyRefs(refs);
	}

	private void addAdministativeInformation(AdministrativeInformation administration, List<ReferenceInfo> refs) {
		if (administration == null) {
			return;
		}
		Optional.ofNullable(administration.getCreator()).map(ReferenceInfos.toReferenceInfo("creator")).ifPresent(refs::add);
	}
	@JsonIgnore
	public List<SubmodelElement> getChildren() {
		return getSubmodelElements();
	}

}
