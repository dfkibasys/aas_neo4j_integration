package de.dfki.cos.aas2graph.kafka.util;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonMapperFactory;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.SimpleAbstractTypeResolverFactory;
import org.eclipse.digitaltwin.aas4j.v3.model.AnnotatedRelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.BasicEventElement;
import org.eclipse.digitaltwin.aas4j.v3.model.Blob;
import org.eclipse.digitaltwin.aas4j.v3.model.Capability;
import org.eclipse.digitaltwin.aas4j.v3.model.Entity;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.MultiLanguageProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Range;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.RelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import de.dfki.cos.aas2graph.kafka.pebble.model.YamlMapperFactory;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleAnnotatedRelationshipElement;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleAssetAdministrationShell;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleBasicEventElement;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleBlob;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleCapability;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleEntity;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleFile;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleMultiLanguageProperty;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleOperation;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleProperty;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleRange;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleReferenceElement;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleRelationshipElement;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleSubmodel;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleSubmodelElementCollection;
import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleSubmodelElementList;

public class AasIo {

	private static JsonMapper MAPPER = null;
	private static YAMLMapper YAML_MAPPER = null;

	private AasIo() {

	}

	public static JsonMapper jsonMapper() {
		if (MAPPER == null) {
			SimpleAbstractTypeResolver typeResolver = new SimpleAbstractTypeResolverFactory().create();
			overrideTypes(typeResolver);
			JsonMapperFactory jsonMapperFactory = new JsonMapperFactory();
			MAPPER = jsonMapperFactory.create(typeResolver);
			MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		}
		return MAPPER;
	}

	public static YAMLMapper yamlMapper() {
		if (YAML_MAPPER == null) {
			SimpleAbstractTypeResolver typeResolver = new SimpleAbstractTypeResolverFactory().create();
			overrideTypes(typeResolver);
			YamlMapperFactory yamlMapperFactory = new YamlMapperFactory();
			YAML_MAPPER = yamlMapperFactory.create(typeResolver);
			YAML_MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		}
		return YAML_MAPPER;
	}

	private static void overrideTypes(SimpleAbstractTypeResolver typeResolver) {
		typeResolver.addMapping(Capability.class, PebbleCapability.class);
		typeResolver.addMapping(Blob.class, PebbleBlob.class);
		typeResolver.addMapping(File.class, PebbleFile.class);
		typeResolver.addMapping(MultiLanguageProperty.class, PebbleMultiLanguageProperty.class);
		typeResolver.addMapping(Property.class, PebbleProperty.class);
		typeResolver.addMapping(Range.class, PebbleRange.class);
		typeResolver.addMapping(ReferenceElement.class, PebbleReferenceElement.class);
		typeResolver.addMapping(Entity.class, PebbleEntity.class);
		typeResolver.addMapping(BasicEventElement.class, PebbleBasicEventElement.class);
		typeResolver.addMapping(Operation.class, PebbleOperation.class);
		typeResolver.addMapping(RelationshipElement.class, PebbleRelationshipElement.class);
		typeResolver.addMapping(AnnotatedRelationshipElement.class, PebbleAnnotatedRelationshipElement.class);
		typeResolver.addMapping(SubmodelElementCollection.class, PebbleSubmodelElementCollection.class);
		typeResolver.addMapping(SubmodelElementList.class, PebbleSubmodelElementList.class);
		typeResolver.addMapping(Submodel.class, PebbleSubmodel.class);
		typeResolver.addMapping(AssetAdministrationShell.class, PebbleAssetAdministrationShell.class);
	}

}
