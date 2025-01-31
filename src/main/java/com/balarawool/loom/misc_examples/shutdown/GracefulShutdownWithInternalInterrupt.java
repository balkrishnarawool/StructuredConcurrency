package com.balarawool.loom.misc_examples.shutdown;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

// Example of graceful shutdown (a shutdown where resources are closed).
// When you run this class, it interrupts the main thread after 5 seconds
// and the scope inside main thread (along with all nested scopes) is closed.
// This means all resources are also closed.
//
// TODO: Create a big hierarchy of scopes and see how resources are closed.
// scope1 (resource1) - scope2 (resource2)
//                    - scope3 (resource3) - scope4 (resource4)
//                                         - scope5 (resource5)
// It seems like they are closed in random order. Is this a problem?
//
public class GracefulShutdownWithInternalInterrupt {

    public static void main(String[] args) {
        var t = Thread.currentThread();
        new Thread(() -> {
            try {
                Thread.sleep(5 * 1000);
                t.interrupt();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        try (var scope = StructuredTaskScope.open(Joiner.anySuccessfulResultOrThrow())) {
            var task1 = scope.fork(() -> useResource(1));
            var task2 = scope.fork(() -> useResource(2));
            var task3 = scope.fork(GracefulShutdownWithInternalInterrupt::useMoreResources);

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

    private record Resource(int id) implements AutoCloseable {
        @Override
        public void close() {
            System.out.println("Closing Resource "+id);
        }
    }
}
