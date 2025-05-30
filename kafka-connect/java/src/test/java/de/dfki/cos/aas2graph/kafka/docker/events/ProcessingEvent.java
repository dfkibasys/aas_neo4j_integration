package de.dfki.cos.aas2graph.kafka.docker.events;

public interface ProcessingEvent {

	boolean isSuccess();

	String message();
	
}
