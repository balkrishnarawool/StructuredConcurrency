package com.balarawool.loom.misc_examples.resources;

// A shutdown hook with resources. So when you send an interrupt (read comments for SimpleShutdownHook),
// the shutdown-hook is triggered, and it also closes the resources.
// 1. Run the class.
// 2. Do "ps -ef | grep 'Shutdown'" to get the PID.
// 3. Do "kill [-2 or -15] PID". Make sure to do this within 20 seconds.
// This shows below output:
// "Using Resource[id=1] and Resource[id=2]"
// "Shutting down"
// "Closing resource 2"
// "Closing resource 1"
//  <Exception Stacktrace>
// "Process finished with exit code 130 (interrupted by signal 2:SIGINT)"
//
// If you do "kill -9 PID", you only see this:
// "Using Resource[id=1] and Resource[id=2]"
// "Process finished with exit code 137 (interrupted by signal 9:SIGKILL)"
//
public class ShutdownHookWithResources {

    public static void main(String... args) {
        var t = Thread.currentThread();
//        new Thread(() -> {
//            try {
//                Thread.sleep(4 * 1000);
//                t.interrupt();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();

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
