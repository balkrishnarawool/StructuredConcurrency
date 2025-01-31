package com.balarawool.loom.misc_examples.shutdown;

import java.util.concurrent.StructuredTaskScope;

// Example of graceful shutdown (a shutdown where resources are closed).
// When you run this class, and call "kill -2 PID" or "kill -15 PID", it interrupts the app (read SimpleShutdownExample)
// and it closes the scope (and nested scopes). It also closes all resources.
//
// It works now but the part (look at GracefulShutdownScope constructor) that ensures the thread is terminated is kinda hacky.
// TODO: Make this^ part better.
// TODO: The order in which Resources are closed, should be proper.
// TODO: Make it work with Virtual Threads. --> Platform thread closes the main thread after shutting down. Virtual thread does not close the main thread after shutting down.
//
public class GracefulShutdownWithExternalInterrupt {

    public static void main(String[] args) {
        try (var scope = StructuredTaskScope.open(gracefulShutdownJoiner())) {
            var task1 = scope.fork(() -> useResource(1));
            var task2 = scope.fork(() -> useResource(2));
            var task3 = scope.fork(GracefulShutdownWithExternalInterrupt::useMoreResources);

            scope.join();

            var result1 = task1.get();
            var result2 = task2.get();
            var result3 = task3.get();

            System.out.println("Results: "+result1+" "+result2+" "+result3);
        } catch (InterruptedException e) {
            System.out.println("Scope interrupted");
//            throw new RuntimeException(e);
        }
    }

    private static int useMoreResources() {
        try (var scope = StructuredTaskScope.open()) {
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
            System.out.println("Done using Resource "+id);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    public static StructuredTaskScope.Joiner gracefulShutdownJoiner() {
        return new GracefulShutdownJoiner();
    }

    private static class GracefulShutdownJoiner implements StructuredTaskScope.Joiner {
        private StructuredTaskScope.Joiner joiner;

        private GracefulShutdownJoiner() {
            joiner = StructuredTaskScope.Joiner.allSuccessfulOrThrow();

            var t = Thread.currentThread();
            Runtime.getRuntime().addShutdownHook(Thread.ofPlatform().unstarted(() -> {
                System.out.println("Shutting down");
                t.interrupt();
//                try {
//                    Thread.sleep(2 * 1000);
////                    t.join();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
                while (t.isInterrupted()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }));
        }

          // Looks like this is not needed and that it should work properly out-of-the-box (i.e. close all resources when closed). Test this.
//        @Override
//        public void close() {
//            System.out.println("Closing scope");
//            super.close();
//        }

        @Override
        public boolean onFork(StructuredTaskScope.Subtask subtask) {
            return joiner.onFork(subtask);
        }

        @Override
        public boolean onComplete(StructuredTaskScope.Subtask subtask) {
            return joiner.onComplete(subtask);
        }

        @Override
        public Object result() throws Throwable {
            return joiner.result();
        }
    }

    private record Resource(int id) implements AutoCloseable {
        @Override
        public void close() {
            System.out.println("Closing Resource "+id);
        }
    }

}
