package org.eclipse.basyx.kafka.connect.neo4j.pebble;

import java.util.Map;
import java.util.Objects;

import org.eclipse.basyx.kafka.connect.neo4j.events.Event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PebbleContext {

	private final Event event;
	private final PebbleContextMeta meta;
	
	@JsonCreator
	public PebbleContext(@JsonProperty("event") Event event, @JsonProperty("meta") PebbleContextMeta meta) {
		this.event = event;
		this.meta = meta;
	}
	
	public Event getEvent() {
		return event;
	}

	public PebbleContextMeta getMeta() {
		return meta;
	}
	
	public Map<String, Object> toMap() {
		return Map.of("event", event, "meta", meta);
	}

	@Override
	public String toString() {
		return "PebbleContext [event=" + event + ", meta=" + meta + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(event, meta);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PebbleContext other = (PebbleContext) obj;
		return Objects.equals(event, other.event) && Objects.equals(meta, other.meta);
	}
}