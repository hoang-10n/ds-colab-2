import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** 
 * Fix 2: Use ConcurrentHashMap with AtomicInteger
 * The cleanest solution is to use a ConcurrentHashMap and store an AtomicInteger for thread-safe increments.
 */

public class ConcurrencyChallengeFix2 {

    // Use ConcurrentHashMap and AtomicInteger
    private static final ConcurrentHashMap<String, AtomicInteger> serverData = new ConcurrentHashMap<>();

    public static void processUpdate(String recordId) {
        // computeIfAbsent ensures only one AtomicInteger per key
        serverData.computeIfAbsent(recordId, k -> new AtomicInteger(0))
                  .incrementAndGet();
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