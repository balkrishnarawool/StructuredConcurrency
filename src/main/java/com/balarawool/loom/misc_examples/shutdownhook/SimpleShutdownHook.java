package com.balarawool.loom.misc_examples.shutdownhook;

// A simple shutdown hook. So when you send an interrupt (read below), the shutdown-hook is triggered.
// 1. Run the class.
// 2. Do "ps -ef | grep 'Shutdown'" to get the PID.
//
// 3a. Use the PID and do "kill -2 PID". This should show "ShutdownHook: App shutting down!" and "Process finished with exit code 130 (interrupted by signal 2:SIGINT)" on console.
// This means "kill -2" sends a SIGINT signal.
//
// 3b. Use the PID and do "kill -9 PID". This should show only "Process finished with exit code 137 (interrupted by signal 9:SIGKILL)" on console.
// This means "kill -9" sends a SIGKILL signal.
//
// 3c. Use the PID and do "kill -15 PID". This should show "ShutdownHook: App shutting down!" and "Process finished with exit code 143 (interrupted by signal 15:SIGTERM)" on console.
// This means "kill -15" sends a SIGTERM signal.
//
public class SimpleShutdownHook {

    public static void main(final String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("ShutdownHook: App shutting down!");})
        );

        runApp();
    }

    private static void runApp() {
        int i = 1;
        while (true) {
            if (i % 10 == 0) System.out.println("."); else System.out.print(".");
            i++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
