package org.eclipse.basyx.kafka.connect.neo4j.pebble;

import java.util.Map;
import java.util.Objects;

public class PebbleContext {

	private Map<String, Object> event;
	private PebbleContextMeta meta;

	public PebbleContext() {
	}
	
	public PebbleContext(Map<String, Object> event, PebbleContextMeta meta) {
		this.event = event;
		this.meta = meta;
	}
	
	public void setEvent(Map<String, Object> event) {
		this.event = event;
	}
	
	public void setMeta(PebbleContextMeta meta) {
		this.meta = meta;
	}
	
	public Map<String, Object> getEvent() {
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