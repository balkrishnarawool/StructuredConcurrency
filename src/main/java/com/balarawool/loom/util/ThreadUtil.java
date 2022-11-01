package com.balarawool.loom.util;

public class ThreadUtil {
    public static void logAndWait(String task, long delay) {
        System.out.println("Thread: " + getThreadName() + " Performing task: " + task+"()");
        try {
            Thread.sleep(delay * 1_000);
            System.out.println("Done task: " + task + "()");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getThreadName() {
        var th = Thread.currentThread();
        return th.isVirtual() ? th.toString() : th.getName();
    }
}
