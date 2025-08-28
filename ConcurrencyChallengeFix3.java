import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** 
 * Fix 3: Use ConcurrentHashMap.merge (atomic update)
 * Java 8+ provides a neat way to use ConcurrentHashMap.merge.
 * Here, merge ensures atomicity internally.
 */

public class ConcurrencyChallengeFix3 {

    private static final ConcurrentHashMap<String, Integer> serverData = new ConcurrentHashMap<>();

    public static void processUpdate(String recordId) {
        serverData.merge(recordId, 1, Integer::sum);
    }

    public static void main(String[] args) throws InterruptedException {
        // Use a thread pool to simulate 1000 concurrent update operations.
        ExecutorService executor = Executors.newFixedThreadPool(100);
        String recordId = "station_123";

        // Submit 1000 tasks to update the same piece of data.
        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> processUpdate(recordId));
        }

        // Wait for all tasks to complete.
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // What is the final value? It should be 1000.
        System.out.println("Final update count for " + recordId + ": " + serverData.get(recordId));
    }
}