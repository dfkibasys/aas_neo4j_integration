import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.kafka.shaded.io.opentelemetry.proto.trace.v1.Status.StatusCode;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.util.Identifiable;

import de.dfki.cos.aas2graph.kafka.pebble.model.json.PebbleSubmodelElement;
import de.dfki.cos.aas2graph.kafka.util.AasIo;

public class Evaluation {
	private static final HttpClient CLIENT = HttpClient.newHttpClient();

	public static void main(String[] args) throws IOException, InterruptedException {

		ArrayList<String> shells = new ArrayList<>();
		ArrayList<String> identifications = new ArrayList<>();
		ArrayList<String> signals = new ArrayList<>();
		
		int from = 10000000;
		int num = 6;
		
		for (int i = from; i < num + from; i++) {
			String shell = newShell(i);
			shells.add(shell);
			String identification = newIdentification(i, i%2 == 0 ? "2.3.0": "2.7.0");
			identifications.add(identification);
		//	System.out.println(countSmes(identification));
			String signal = newSignals(i);
	//		System.out.println(countSmes(signal));
			signals.add(signal);
		}
		long start = System.nanoTime();
		for (int i = 0; i < num; i++) {
			pushShell("http://aas.dfki.de/ids/aas/mir100_" + i,  shells.get(i));
		//	Thread.sleep(10);
			pushSubmodel("http://aas.dfki.de/ids/sm/identification_" + i, identifications.get(i));
			pushSubmodel("http://aas.dfki.de/ids/sm/signals" + i, signals.get(i));
		//	Thread.sleep(10);
		}
		long end = System.nanoTime();
		System.out.println((end - start) / 1_000_000);
	}

	private static int countSmes(String sme) throws JsonMappingException, JsonProcessingException {
		Submodel sm = AasIo.jsonMapper().readValue(sme, Submodel.class);
		int count = 0;
		for (SubmodelElement eachSm : sm.getSubmodelElements()) {
			count ++;
			count += countSmes((PebbleSubmodelElement) eachSm);
		}
		return count;
	}

	private static int countSmes(PebbleSubmodelElement sme) {
		int count = 0;
		for (SubmodelElement eachSm : sme.getChildren()) {
			count++;
			count += countSmes((PebbleSubmodelElement)eachSm);
		}
		return count;
	}

	private static void pushShell(String id, String shell) throws IOException, InterruptedException {
	    HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/shells"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(shell))
                .build();
	    CLIENT.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
	}

	private static void pushSubmodel(String id, String submodel) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/submodels"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(submodel))
                .build();
		CLIENT.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
		
	}

	private static String newShell(int i) throws IOException {
		String value = new String(Files.readAllBytes(Path.of("./src/test/resources/evaluation/shell.json")), StandardCharsets.UTF_8);
		return value.replaceAll("%i%", "" + i);
	}

	private static String newIdentification(int i, String version) throws IOException {
		String value = new String(Files.readAllBytes(Path.of("./src/test/resources/evaluation/identification.json")), StandardCharsets.UTF_8);
		value = value.replaceAll("%i%", "" + i);
		return value.replaceAll("%version%", "" + version);
	}
	
	private static String newSignals(int i) throws IOException {
		String value = new String(Files.readAllBytes(Path.of("./src/test/resources/evaluation/signal.json")), StandardCharsets.UTF_8);
		return value = value.replaceAll("%i%", "" + i);
	}
	

}
