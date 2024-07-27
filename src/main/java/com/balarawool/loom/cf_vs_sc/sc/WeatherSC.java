package com.balarawool.loom.cf_vs_sc.sc;

import com.balarawool.loom.util.WeatherUtil;
import com.balarawool.loom.util.WeatherUtil.Weather;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public class WeatherSC {
    public static void getWeather() {
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<Weather>()) {
            scope.fork(() -> WeatherUtil.getWeatherFromSource1("Cologne"));
            scope.fork(() -> WeatherUtil.getWeatherFromSource2("Cologne"));
            scope.fork(() -> WeatherUtil.getWeatherFromSource3("Cologne"));

            var weather = scope.join().result();
            System.out.println(weather);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
