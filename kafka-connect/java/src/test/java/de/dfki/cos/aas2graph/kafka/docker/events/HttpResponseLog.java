package de.dfki.cos.aas2graph.kafka.docker.events;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpResponseLog implements ProcessingEvent {

	private static final Pattern PATTERN = Pattern
			.compile("statusCode=(\\d+).*statusMessage='([^']+).*responseBody='([^']+)");

	private Integer statusCode;
	private String statusMessage;
	private String responseBody;

	public Integer getStatusCode() {
		return statusCode;
	}

	@Override
	public boolean isSuccess() {
		return statusCode >= 200 && statusCode < 300;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	@Override
	public String message() {
		return "HttpResponseLog [statusCode=" + statusCode + ", statusMessage=" + statusMessage + ", responseBody="
				+ responseBody + "]";
	}

	@Override
	public String toString() {
		return (isSuccess() ? "Success: " : "Failure: ") + message();
	}

	public static Optional<HttpResponseLog> fromLog(String log) {
		System.out.println(log);
		if (!log.contains("HttpResponse")) {
			return Optional.empty();
		}
		HttpResponseLog logObject = new HttpResponseLog();
		Matcher matcher = PATTERN.matcher(log);
		if (matcher.find()) {
			logObject.setStatusCode(Integer.parseInt(matcher.group(1)));
			logObject.setStatusMessage(matcher.group(2));
			logObject.setResponseBody(matcher.group(3));
		} else {
			throw new RuntimeException("Failed to parse log message: " + log);
		}

		return Optional.of(logObject);
	}

}
