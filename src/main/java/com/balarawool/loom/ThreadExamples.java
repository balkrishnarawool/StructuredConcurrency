package com.balarawool.loom;

import java.util.concurrent.Executors;

public class ThreadExamples {
    public static void platformThread() {
    	var t = Thread.ofPlatform().start(() -> System.out.println(Thread.currentThread()));
    }

    public static void virtualThread() {
    	try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    		executor.submit(() -> System.out.println(Thread.currentThread()));
    		executor.submit(() -> System.out.println(Thread.currentThread()));
    		executor.submit(() -> System.out.println(Thread.currentThread()));
    		executor.submit(() -> System.out.println(Thread.currentThread()));
    		executor.submit(() -> System.out.println(Thread.currentThread()));
    		executor.submit(() -> System.out.println(Thread.currentThread()));
    		executor.submit(() -> System.out.println(Thread.currentThread()));
    	}
    }
}
