package com.balarawool.loom.util;

import static com.balarawool.loom.util.ThreadUtil.logAndWait;

public class WeatherUtil {

    public static Weather getWeatherFromSource1(String city) {
        logAndWait("getWeatherFromSource1");
        return new Weather("25 C");
    }

    public static Weather getWeatherFromSource2(String city) {
        logAndWait("getWeatherFromSource2");
        return new Weather("26 C");
    }

    public static Weather getWeatherFromSource3(String city) {
        logAndWait("getWeatherFromSource3");
        return new Weather("27 C");
    }

    public record Weather(String temperature) {}

}