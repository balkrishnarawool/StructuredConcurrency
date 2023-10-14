package com.balarawool.loom.util;

import static com.balarawool.loom.util.ThreadUtil.logAndWait;

public class WeatherUtil {

    public static int getTemperatureFromSource1() {
    	throw new RuntimeException();
//        logAndWait("getTemperatureFromSource1");
//        return randomTemp("getTemperatureFromSource1");
    }

	public static int getTemperatureFromSource2() {
        logAndWait("getTemperatureFromSource2");
        return randomTemp("getTemperatureFromSource2");
    }

    public static int getTemperatureFromSource3() {
        logAndWait("getTemperatureFromSource3");
        return randomTemp("getTemperatureFromSource3");
    }
    
    private static int randomTemp(String task) {
    	int temp = (int) (20 + 10 * Math.random());
        System.out.println(task + " returns " +  temp);
        return temp;
	}


    public record Weather(int temperature) {}

}