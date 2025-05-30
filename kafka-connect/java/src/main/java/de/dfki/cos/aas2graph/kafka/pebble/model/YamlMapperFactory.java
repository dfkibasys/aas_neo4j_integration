package de.dfki.cos.aas2graph.kafka.pebble.model;

import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.deserialization.EnumDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.serialization.EnumSerializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.util.ReflectionHelper;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.internal.ReflectionAnnotationIntrospector;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper.Builder;

public class YamlMapperFactory {

	public YAMLMapper create(SimpleAbstractTypeResolver typeResolver) {
		Builder builder = YAMLMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
				.annotationIntrospector(new ReflectionAnnotationIntrospector()).serializationInclusion(JsonInclude.Include.NON_NULL);

		getModulesToInstall(typeResolver).stream().forEach(m -> builder.addModule(m));

		YAMLMapper mapper = builder.build();
		ReflectionHelper.JSON_MIXINS.entrySet().forEach(x -> mapper.addMixIn(x.getKey(), x.getValue()));

		return mapper;
	}

	protected List<SimpleModule> getModulesToInstall(SimpleAbstractTypeResolver typeResolver) {
		return Arrays.asList(buildEnumModule(), buildImplementationModule(typeResolver));
	}

	protected SimpleModule buildImplementationModule(SimpleAbstractTypeResolver typeResolver) {
		SimpleModule module = new SimpleModule();
		module.setAbstractTypes(typeResolver);
		return module;
	}

	protected SimpleModule buildEnumModule() {
		SimpleModule module = new SimpleModule();
		ReflectionHelper.ENUMS.forEach(x -> module.addSerializer(x, new EnumSerializer()));
		ReflectionHelper.ENUMS.forEach(x -> module.addDeserializer(x, new EnumDeserializer<>(x)));
		return module;
	}
}