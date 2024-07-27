package com.balarawool.loom.misc_examples.shutdownhook;

// Simple ShutdownHook with VirtualThread
public class ShutdownHookWithVirtualThread {

    public static void main(String[] args) {
        try {
            Thread.ofVirtual().name("MainVirtualThread").start(() -> runAndAddShutdownHook()).join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runAndAddShutdownHook() {
        var t = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(Thread.ofVirtual().unstarted(() -> {
            System.out.println("Shutting down");
            t.interrupt();
        }));

        while (true) {
            try {
                Thread.sleep(1 * 1000);
                System.out.print(".");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
