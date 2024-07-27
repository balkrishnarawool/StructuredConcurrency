package com.balarawool.loom.misc_examples.shutdown;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

// Example of hard shutdown (a shutdown where resources are not closed).
// Please read comments in SimpleShutdownHook class first.
// If you run this class and then send signals using "kill -2 PID", "kill -9 PID" or "kill -15 PID", it doesn't close resources.
public class HardShutdownWithExternalInterrupt {

    public static void main(String[] args) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var task1 = scope.fork(() -> useResource(1));
            var task2 = scope.fork(() -> useResource(2));
            var task3 = scope.fork(HardShutdownWithExternalInterrupt::useMoreResources);

            scope.join().throwIfFailed();

            var result1 = task1.get();
            var result2 = task2.get();

            System.out.println("Results: "+result1+" "+result2);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static int useMoreResources() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var task1 = scope.fork(() -> useResource(3));
            var task2 = scope.fork(() -> useResource(4));

            scope.join().throwIfFailed();

            var result1 = task1.get();
            var result2 = task2.get();

            System.out.println("Results: "+result1+" "+result2);
            return 30;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static int useResource(int id) {
        try(var resource = new Resource(id)) {
            System.out.println("Using "+resource);
            Thread.sleep(20 * 1000);
            System.out.println("Done using Resource1");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    private record Resource(int id) implements AutoCloseable {
        @Override
        public void close() {
            System.out.println("Closing Resource "+id);
        }
    }
}
