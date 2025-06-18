import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParserAverage {
    static class Stats {
        int count = 0;
        long transformationSum = 0;
        long httpCypherSum = 0;
        long totalSum = 0;

        void add(int transformation, int httpCypher, int total) {
            count++;
            transformationSum += transformation;
            httpCypherSum += httpCypher;
            totalSum += total;
        }

        double avg(long sum) { return count == 0 ? 0 : (double) sum / count; }
    }

    public static void main(String[] args) throws IOException {
        String logFile = "../../eval_update_sleep_100.txt";
   //     String logFile = "../../eval_1000_26679.txt";

        Pattern pattern = Pattern.compile(
            "\\[(.*?)\\] INFO EVAL-RECORD-(aas-events|submodel-events)-(.*?)-transformation(\\d+)ms-httpCypher(\\d+)-total(\\d+)-sinceInsertion(\\d+)"
        );

        Map<String, Stats> groups = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher m = pattern.matcher(line);
                if (m.find()) {
                    String eventType = m.group(2); // aas-events oder submodel-events
                    String modelId = m.group(3); // mit oder ohne Submodell

                    String groupKey = eventType;
                    if (eventType.equals("submodel-events")) {
                       Matcher submodelMatcher = Pattern.compile("sm/([a-zA-Z_]+)_\\d+").matcher(modelId);
                        if (submodelMatcher.find()) {
                            groupKey += "-" + submodelMatcher.group(1);
                        } else {
                            groupKey += "-other";
                        }
                    }

                    int transformation = Integer.parseInt(m.group(4));
                    int httpCypher = Integer.parseInt(m.group(5));
                    int total = Integer.parseInt(m.group(6));

                    groups.computeIfAbsent(groupKey, k -> new Stats()).add(transformation, httpCypher, total);
                }
            }
        }

        // Ausgabe
        System.out.println("Durchschnittswerte pro Gruppe:");
        for (Map.Entry<String, Stats> entry : groups.entrySet()) {
            String key = entry.getKey();
            Stats stats = entry.getValue();
            System.out.printf("%-30s: transformation=%.2f ms | httpCypher=%.2f | total=%.2f (N=%d)%n",
                key,
                stats.avg(stats.transformationSum),
                stats.avg(stats.httpCypherSum),
                stats.avg(stats.totalSum),
                stats.count
            );
        }
    }
}
