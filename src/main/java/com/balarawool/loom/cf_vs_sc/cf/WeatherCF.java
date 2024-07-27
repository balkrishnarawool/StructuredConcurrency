package com.balarawool.loom.cf_vs_sc.cf;

import com.balarawool.loom.util.WeatherUtil;

import java.util.concurrent.CompletableFuture;

public class WeatherCF {
    public static void getWeather() {
        var future1 = CompletableFuture.supplyAsync(() -> WeatherUtil.getWeatherFromSource1("Cologne"));
        var future2 = CompletableFuture.supplyAsync(() -> WeatherUtil.getWeatherFromSource2("Cologne"));
        var future3 = CompletableFuture.supplyAsync(() -> WeatherUtil.getWeatherFromSource3("Cologne"));

        CompletableFuture.anyOf(future1, future2, future3)
                .exceptionally(th -> {
                    throw new RuntimeException(th);
                })
                .thenAccept(weather -> System.out.println("Weather: " + weather))
                .join();
    }
}