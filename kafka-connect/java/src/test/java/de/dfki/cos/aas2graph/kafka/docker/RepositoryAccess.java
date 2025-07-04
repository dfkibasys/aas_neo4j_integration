package de.dfki.cos.aas2graph.kafka.docker;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.Identifiable;
import org.junit.Assert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.dfki.cos.aas2graph.kafka.util.AasIo;

public class RepositoryAccess<T extends Identifiable> {

	private final String baseUri;
	private final Class<T> cls;
	private final HttpClient client;
	
	public RepositoryAccess(Class<T> cls, String baseUri) {
		this.client = HttpClient.newHttpClient();
		this.cls = cls;		
		this.baseUri = baseUri;
	}

	public List<T> getAll() throws IOException, InterruptedException, DeserializationException {
		URI uri = URI.create(baseUri + "?limit="+ Integer.MAX_VALUE);
		HttpRequest request = HttpRequest.newBuilder().uri(uri)
				.header("Accept", "application/json").GET().build();
		HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
		Assert.assertEquals(200, response.statusCode());
		ObjectMapper mapper = AasIo.jsonMapper();
		try (InputStream in = response.body();
				BufferedInputStream bIn = new BufferedInputStream(in)) {
			JsonNode resultMapper = mapper.readValue(in, JsonNode.class);
			JsonNode resultNode = resultMapper.get("result");
			return mapper.treeToValue(resultNode, mapper.getTypeFactory().constructCollectionLikeType(List.class, cls));
		}
	}

	public void cleanUp() throws IOException, InterruptedException, DeserializationException {
		List<T> items = getAll();
		for (T eachItem : items) {
			deleteById(eachItem.getId());
		}
	}
	
	private void deleteById(String id) throws IOException, InterruptedException {		
		String base64UrlEncodedId = Base64.getUrlEncoder().encodeToString(id.getBytes(StandardCharsets.UTF_8));		
		URI uri = URI.create(baseUri + "/" + base64UrlEncodedId);
		HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").DELETE()
				.build();
		HttpResponse<Void> response =  client.send(request, HttpResponse.BodyHandlers.discarding());
		Assert.assertEquals(204, response.statusCode());
	}

	public T post(T item) throws IOException, InterruptedException, SerializationException, DeserializationException {
		ObjectMapper mapper = AasIo.jsonMapper();
		String body = mapper.writeValueAsString(item);
		String result = post(body);
		return mapper.readValue(result, cls);
	}
	
	public <U> void putUnderPath(String path, U item) throws IOException, InterruptedException, SerializationException, DeserializationException {
		ObjectMapper mapper = AasIo.jsonMapper();
		String body = mapper.writeValueAsString(item);
		put(path, body);
		
	}
	
	private String put(String path, String body) throws IOException, InterruptedException {
		URI uri = URI.create(baseUri + '/' + path );
		HttpRequest request = HttpRequest.newBuilder().uri(uri)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); 
		Assert.assertEquals(response.body(), 201, response.statusCode());
		
		return response.body();
	}
	
	private String post(String body) throws IOException, InterruptedException {
		URI uri = URI.create(baseUri);
		HttpRequest request = HttpRequest.newBuilder().uri(uri)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); 
		Assert.assertEquals(response.body(), 201, response.statusCode());
		
		return response.body();
	}
	
}