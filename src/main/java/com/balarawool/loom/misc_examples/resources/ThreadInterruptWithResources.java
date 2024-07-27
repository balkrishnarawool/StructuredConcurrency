package com.balarawool.loom.misc_examples.resources;

// An example of Thread interrupt with resources.
// So when you run the class below, it starts main thread with some Resource-s
// and interrupts the main thread after 4 seconds and closes the resources.
// 1. Run the class.
// This shows below output:
// "Using Resource[id=1] and Resource[id=2]"
// "Closing resource 2"
// "Closing resource 1"
// "Shutting down"
//  <Exception Stacktrace>
//
// Note: Although there is a ShutdownHook with ability to interrupt main thread and close resources,
// the interrupt and resource-closing is done by child-thread.
// The shutdownHook gets executed while the application is shutting down and just prints "Shutting down"
//
public class ThreadInterruptWithResources {

    public static void main(String... args) {
        var t = Thread.currentThread();
        new Thread(() -> {
            try {
                Thread.sleep(4 * 1000);
                t.interrupt();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down");
            t.interrupt();
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));

        try (var resource1 = new Resource(1);
             var resource2 = new Resource(2)) {
            System.out.println("Using "+resource1 +" and "+resource2);
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private record Resource(int id) implements AutoCloseable {
        @Override
        public void close() {
            System.out.println("Closing resource "+id);
        }
    }
}
