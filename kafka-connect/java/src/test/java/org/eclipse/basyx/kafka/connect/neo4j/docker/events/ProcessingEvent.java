package org.eclipse.basyx.kafka.connect.neo4j.docker.events;

public interface ProcessingEvent {

	boolean isSuccess();

	String message();
	
}
