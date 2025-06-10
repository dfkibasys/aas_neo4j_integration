package de.dfki.cos.aas2graph.kafka;

import java.io.IOException;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.transforms.Transformation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.dfki.cos.aas2graph.kafka.events.Event;
import de.dfki.cos.aas2graph.kafka.pebble.HttpRequestBodyGenerator;
import de.dfki.cos.aas2graph.kafka.pebble.PebbleContext;
import de.dfki.cos.aas2graph.kafka.pebble.PebbleContextMeta;
import de.dfki.cos.aas2graph.kafka.util.AasIo;
import de.dfki.cos.aas2graph.kafka.util.YamlJsonConverter;


public class CypherTransformation<R extends ConnectRecord<R>> implements Transformation<R> {

	private final HttpRequestBodyGenerator generator = new HttpRequestBodyGenerator();

	private final YamlJsonConverter converter = new YamlJsonConverter();

	@Override
	public R apply(R record) {
		try {
			PebbleContext context = context(record);
			String bodyYaml = generator.generateYamlFromMessage(context);
			String requestBody = converter.yamlToJson(bodyYaml);
			return record.newRecord(record.topic(), record.kafkaPartition(), record.keySchema(), record.key(), Schema.STRING_SCHEMA, requestBody, record.timestamp());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private PebbleContext context(R record) throws JsonMappingException, JsonProcessingException {
		String sEvent = (String) record.value();

		PebbleContextMeta meta = metaData(record);
		Event event = AasIo.jsonMapper().readValue(sEvent, Event.class);
		return new PebbleContext(event, meta);
	}

	private PebbleContextMeta metaData(R record) {
		PebbleContextMeta meta = new PebbleContextMeta();
		meta.setSourceUrl(System.getenv("BASYX_AAS_REPO_URL"));
		meta.setRegistrationTime(record.timestamp());
		meta.setTopic(record.topic());
		return meta;
	}

	@Override
	public void close() {
	}

	@Override
	public void configure(Map<String, ?> configs) {
	}

	@Override
	public ConfigDef config() {
		return new ConfigDef();
	}

}