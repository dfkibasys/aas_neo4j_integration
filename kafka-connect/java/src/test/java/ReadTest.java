import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;

import de.dfki.cos.aas2graph.kafka.util.AasIo;

public class ReadTest {

	public static void main2(String[] args) throws IOException, InterruptedException {

		HttpClient client = HttpClient.newHttpClient();

		long start = System.currentTimeMillis();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081/shells?limit=100")).header("Content-Type", "application/json").GET().build();

		HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
		GetAssetAdministrationShellsResult result;
		try (BufferedInputStream bIn = new BufferedInputStream(response.body())) {
			result = AasIo.jsonMapper().readValue(bIn, GetAssetAdministrationShellsResult.class);
		}
		List<String> ids = new ArrayList<String>();

		// result.getResult().forEach(s-> System.out.println(s.getId()));
		for (AssetAdministrationShell shell : result.getResult()) {
			for (Reference eachRef : shell.getSubmodels()) {
				String url = eachRef.getKeys().get(0).getValue();
				request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081/submodels/" + Base64.getUrlEncoder().encodeToString(url.getBytes()))).header("Content-Type", "application/json").GET().build();
				HttpResponse<InputStream> inner = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
				try (BufferedInputStream bIn = new BufferedInputStream(inner.body())) {
					Submodel sm = AasIo.jsonMapper().readValue(bIn, Submodel.class);
					if (sm.getIdShort().equals("Identification")) {
						String manufacturer = null;
						String product = null;
						String version = null;
						for (SubmodelElement eachElem : sm.getSubmodelElements()) {
							if (!(eachElem instanceof Property)) {
								continue;
							}
							Property prop = (Property) eachElem;
							Reference ref = eachElem.getSemanticId();
							if (ref != null) {
								switch (ref.getKeys().get(0).getValue()) {
								case "0173-1#02-AAO677#002":
									manufacturer = prop.getValue();
									break;
								case "0173-1#02-AAW338#001":
									product = prop.getValue();
									break;
								case "0173-1#02-AAS383#003":
									version = prop.getValue();
									break;
								}
							}

						}

						if (isVersionLessThan_2_5_2(version)) {
							ids.add(shell.getId());
							// System.out.println(manufacturer + "-" + product + "-" + version);
						}

						break;
					}
				}
			}
		}

		// for (int i = 0; i < 100; i++) {
		// response = client.send(request, HttpResponse.BodyHandlers.ofString());
		// }
		long end = System.currentTimeMillis();

		System.out.println("Dauer: " + ((end - start) / 100) + " ms");
		System.out.println("Ergebnis: " + response.body());
		System.out.println(ids.size());
	}

	public static boolean isVersionLessThan_2_5_2(int major, int minor, int patch) {
		if (major < 2)
			return true;
		if (major > 3)
			return false;
		// major == 3
		if (minor < 5)
			return true;
		if (minor > 5)
			return false;
		// minor == 5
		return patch < 2;
	}

	/**
	 * Alternativ: Übergabe als String z.B. "3.5.1"
	 */
	public static boolean isVersionLessThan_2_5_2(String version) {
		String[] parts = version.split("\\.");
		int major = Integer.parseInt(parts[0]);
		int minor = Integer.parseInt(parts[1]);
		int patch = Integer.parseInt(parts[2]);
		return isVersionLessThan_2_5_2(major, minor, patch);
	}

	public static void main(String[] args) throws IOException,
	 InterruptedException {
	 String url = "http://localhost:7474/db/neo4j/tx/commit";
	 String user = "neo4j";
	 String password = "your_password";
	
	 String cypher = "MATCH (a:AssetAdministrationShell)-[:HAS_SUBMODEL]->(s:Submodel) "
	 + "MATCH (s)-[:HAS_ELEMENT]->(pMf)-[:HAS_SEMANTIC]->(:SemanticConcept {id: '0173-1#02-AAO677#002'}) "
	 + "MATCH (s)-[:HAS_ELEMENT]->(pPd)-[:HAS_SEMANTIC]->(:SemanticConcept {id: '0173-1#02-AAW338#001'}) "
	 + "MATCH (s)-[:HAS_ELEMENT]->(pVers)-[:HAS_SEMANTIC]->(:SemanticConcept {id: '0173-1#02-AAS383#003'}) "
	 + "WHERE pPd.value = 'MiR100' "
	 + "count a.id, pVers.value ";
	 
	 String payload = "{ \"statements\": [ { \"statement\": \"" +
	 cypher.replace("\"", "\\\"") + "\" } ] }";
	// System.out.println(payload);
	 
	 HttpClient client = HttpClient.newHttpClient();
	 HttpRequest request = HttpRequest.newBuilder()
	 .uri(URI.create(url))
	 .header("Content-Type", "application/json")
	 .POST(HttpRequest.BodyPublishers.ofString(payload))
	 .build();
	
	 HttpResponse<String> response = client.send(request,
	 HttpResponse.BodyHandlers.ofString());
	 Thread.sleep(100);
	 long start = System.currentTimeMillis();
	 for (int i = 0; i < 100; i++) {
	 response = client.send(request, HttpResponse.BodyHandlers.ofString());
	 }
	 long end = System.currentTimeMillis();
	
	 System.out.println("Dauer: " + ((end - start) / 100) + " ms");
	 System.out.println("Ergebnis: " + response.body());
	 }

}
