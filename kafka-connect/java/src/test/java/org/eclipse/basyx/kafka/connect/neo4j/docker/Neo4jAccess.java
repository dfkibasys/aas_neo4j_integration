package org.eclipse.basyx.kafka.connect.neo4j.docker;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.Assert;

public class Neo4jAccess {

	private String httpUrl;
	private HttpClient client;

	public Neo4jAccess(String httpUrl) {
		this.httpUrl = httpUrl;
		client = HttpClient.newHttpClient();
	}

	public String cleanUp() throws IOException, InterruptedException {
		return sendTransactionRequest("MATCH (n) DETACH DELETE n");
	}

	public String getData() throws IOException, InterruptedException {
		return sendTransactionRequest("MATCH (n)-[r]->(m) RETURN n, r, m");
	}

	public String sendTransactionRequest(String statement) throws IOException, InterruptedException {
		String body = String.format("{\"statements\":[{\"statement\":\"%s\",\"resultDataContents\":[\"graph\"]}]}", statement);
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(httpUrl))
				.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		Assert.assertEquals(200, response.statusCode());
		return response.body();
	}

}
