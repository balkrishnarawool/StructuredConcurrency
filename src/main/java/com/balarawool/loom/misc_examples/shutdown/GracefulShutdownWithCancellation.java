package com.balarawool.loom.misc_examples.shutdown;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

// Example of graceful shutdown (a shutdown where resources are closed).
// When you run this class, it creates a scope (with nested scope) and one of the tasks in this scope throws Exception, so it gets cancelled.
// It closes the scope (and nested scopes) and closes all resources.
public class GracefulShutdownWithCancellation {

    public static void main(String[] args) {
        try (var scope = StructuredTaskScope.open(Joiner.anySuccessfulResultOrThrow())) {
            var task1 = scope.fork(() -> useResource(1));
            var task2 = scope.fork(() -> useResourceWithError(2));
            var task3 = scope.fork(GracefulShutdownWithCancellation::useMoreResources);

            scope.join();

            var result1 = task1.get();
            var result2 = task2.get();

            System.out.println("Results: "+result1+" "+result2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static int useMoreResources() {
        try (var scope = StructuredTaskScope.open(Joiner.anySuccessfulResultOrThrow())) {
            var task1 = scope.fork(() -> useResource(3));
            var task2 = scope.fork(() -> useResource(4));

            scope.join();

            var result1 = task1.get();
            var result2 = task2.get();

            System.out.println("Results: "+result1+" "+result2);
            return 30;
        } catch (InterruptedException e) {
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

    private static int useResourceWithError(int id) {
        try(var resource = new Resource(id)) {
            System.out.println("Using "+resource);
            Thread.sleep(5 * 1000);
            throw new RuntimeException("Exception while using "+resource);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private record Resource(int id) implements AutoCloseable {
        @Override
        public void close() {
            System.out.println("Closing Resource "+id);
        }
    }
}
