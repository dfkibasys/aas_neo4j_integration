package de.dfki.cos.aas2graph.kafka.pebble.model.json;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfo;
import de.dfki.cos.aas2graph.kafka.pebble.model.ReferenceInfos;

public interface PebbleModel {

	@JsonIgnore
	default List<String> getLabels() {
		List<String> labels = new ArrayList<>();
		applyLabels(labels);
		labels.sort(Comparator.naturalOrder());
		return labels;
	}
	
	@JsonIgnore
	default List<ReferenceInfo> getRefs() {
		List<ReferenceInfo> refs = new ArrayList<>();
		applyRefs(refs);
		return ReferenceInfos.sort(refs);
	}
	
	void applyLabels(List<String> labels);
	
	void applyRefs(List<ReferenceInfo> refs);
}
