import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** 
 * Fix 1: Use synchronized (simpler but less scalable)
 * If you want minimal changes to your original code, you can update the processUpdate to be synchronized.
 * This works, but all threads are serialized when calling processUpdate, so you lose concurrency benefits.
 */

public class ConcurrencyChallengeFix1 {

    // This HashMap simulates a server's shared, in-memory data store.
    private static final Map<String, Integer> serverData = new HashMap<>();

    // TODO: This method is NOT thread-safe!
    // It reads a value, increments it, and writes it back.
    public static synchronized void processUpdate(String recordId) {
        Integer currentValue = serverData.get(recordId);
        if (currentValue == null) {
            // First time seeing this record, initialize to 1.
            serverData.put(recordId, 1);
        } else {
            // Increment the existing value.
            serverData.put(recordId, currentValue + 1);
        }
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