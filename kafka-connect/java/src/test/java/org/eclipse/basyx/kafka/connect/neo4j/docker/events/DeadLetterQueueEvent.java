package org.eclipse.basyx.kafka.connect.neo4j.docker.events;

public class DeadLetterQueueEvent implements ProcessingEvent {

	private final String evt;
	
	public DeadLetterQueueEvent(String evt) {
		this.evt = evt;
	}
	
	@Override
	public boolean isSuccess() {
		return false;
	}
	
	@Override
	public String message() {
		return "Message sent to dead letter queue: " + evt;
	}
	
	@Override
	public String toString() {
		return isSuccess() ? "Success: " + "Failure: " : message();
	}

}
