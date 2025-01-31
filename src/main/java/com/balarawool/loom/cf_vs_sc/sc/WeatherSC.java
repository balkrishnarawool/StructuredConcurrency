package com.balarawool.loom.cf_vs_sc.sc;

import com.balarawool.loom.util.WeatherUtil;
import com.balarawool.loom.util.WeatherUtil.Weather;

import java.util.concurrent.StructuredTaskScope;

import static java.util.concurrent.StructuredTaskScope.*;

public class WeatherSC {
    public static void getWeather() {
        try (var scope = StructuredTaskScope.open(Joiner.<Weather>anySuccessfulResultOrThrow())) {
            scope.fork(() -> WeatherUtil.getWeatherFromSource1("Cologne"));
            scope.fork(() -> WeatherUtil.getWeatherFromSource2("Cologne"));
            scope.fork(() -> WeatherUtil.getWeatherFromSource3("Cologne"));

            var weather = scope.join();
            System.out.println(weather);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
