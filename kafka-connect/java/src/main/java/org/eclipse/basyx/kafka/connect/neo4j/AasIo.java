package org.eclipse.basyx.kafka.connect.neo4j;

import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.YamlMapperFactory;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json.PebbleAnnotatedRelationshipElement;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json.PebbleAssetAdministrationShell;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json.PebbleBasicEventElement;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json.PebbleBlob;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json.PebbleCapability;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json.PebbleEntity;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json.PebbleFile;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json.PebbleMultiLanguageProperty;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json.PebbleOperation;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json.PebbleProperty;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json.PebbleRange;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json.PebbleReferenceElement;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json.PebbleRelationshipElement;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json.PebbleSubmodel;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json.PebbleSubmodelElementCollection;
import org.eclipse.basyx.kafka.connect.neo4j.pebble.model.json.PebbleSubmodelElementList;
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
