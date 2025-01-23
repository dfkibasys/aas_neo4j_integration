package org.eclipse.basyx.kafka.connect.neo4j.meta;

import java.util.Objects;

public class PebbleContextMeta {

	private String sourceUrl;
	private Long registrationTime;
	private String topic;

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public void setRegistrationTime(Long timestamp) {
		this.registrationTime = timestamp;
	}

	public Long getRegistrationTime() {
		return registrationTime;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTopic() {
		return topic;
	}

	@Override
	public String toString() {
		return "PebbleContextMeta [sourceUrl=" + sourceUrl + ", registrationTime=" + registrationTime + ", topic="
				+ topic + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(registrationTime, sourceUrl, topic);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PebbleContextMeta other = (PebbleContextMeta) obj;
		return Objects.equals(registrationTime, other.registrationTime) && Objects.equals(sourceUrl, other.sourceUrl)
				&& Objects.equals(topic, other.topic);
	}

}
