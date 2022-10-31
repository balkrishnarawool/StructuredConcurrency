package com.balarawool.loom;

import jdk.incubator.concurrent.StructuredTaskScope;

import java.util.concurrent.ExecutionException;

public class Main {

    public static void simpleScope() {
        Thread.ofVirtual().start(() -> System.out.println("Hello"));

        try (var scope = new StructuredTaskScope<>()) {
            var future = scope.fork(() -> waitFor(1).andReturn("Hello"));
            var future2 = scope.fork(() -> waitFor(10).andReturn("World"));
            scope.join();
            System.out.println(future.get());
            System.out.println(future2.get());

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static X waitFor(int delay) {
        return new X(delay);
    }

    private record X(int delay) {
        public String andReturn(String message) {
            System.out.println(Thread.currentThread());
            System.out.println("waiting for "+ delay +" seconds");
            try {
                Thread.sleep(delay * 1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return message;
        }
    }
}