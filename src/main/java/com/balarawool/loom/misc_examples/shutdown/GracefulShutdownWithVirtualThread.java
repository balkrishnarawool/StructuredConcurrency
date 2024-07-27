package com.balarawool.loom.misc_examples.shutdown;

import java.util.concurrent.StructuredTaskScope;

// ShutdownHook that closes Resource-s from (nested) Scope-s with VirtualThread
public class GracefulShutdownWithVirtualThread {

    public static void main(String[] args) throws InterruptedException {
        Thread.ofVirtual().name("MainVirtualThread").start(() -> runWithScopeAndAddShutdownHook()).join();
    }

    private static void runWithScopeAndAddShutdownHook() {
        var t = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(Thread.ofVirtual().unstarted(() -> {
            System.out.println("Shutting down");
            t.interrupt();
            while (t.isInterrupted()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }));

        try (var scope = new StructuredTaskScope()) {
            var task1 = scope.fork(() -> useResource(1));
            var task2 = scope.fork(() -> useResource(2));

            try (var scope2 = new StructuredTaskScope()) {
                var task3 = scope2.fork(() -> useResource(3));
                var task4 = scope2.fork(() -> useResource(4));

                scope2.join();

                var r3 = task3.get();
                var r4 = task4.get();
            } catch (InterruptedException e2) {
                throw new RuntimeException();
            }

            scope.join();

            var r1 = task1.get();
            var r2 = task2.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static int useResource(int i) {
        try (var resource = new Resource(i)) {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return i;
    }

    record Resource(int id) implements AutoCloseable {
        @Override
        public void close() {
            System.out.println("Closing resource with id "+id);
        }
    }
}
