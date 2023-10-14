package com.balarawool.loom;

import com.balarawool.loom.util.CustomerUtil;
import com.balarawool.loom.util.CustomerUtil.CustomerDetails;
import com.balarawool.loom.util.EventUtil;
import com.balarawool.loom.util.WeatherUtil;

import java.util.concurrent.CompletableFuture;
public class CFExamples {
	public static void getAverageTemperature() {
        var future1 = CompletableFuture.supplyAsync(WeatherUtil::getTemperatureFromSource1);
        var future2 = CompletableFuture.supplyAsync(WeatherUtil::getTemperatureFromSource2);
        var future3 = CompletableFuture.supplyAsync(WeatherUtil::getTemperatureFromSource3);

        CompletableFuture.allOf(future1, future2, future3)
                .exceptionally(th -> {
                    throw new RuntimeException(th);
                })
                .thenAccept(ignored -> {
                	var temp1 = future1.join();
                	var temp2 = future2.join();
                	var temp3 = future3.join();
                	
                	System.out.println("Average temperature: " + (double)(temp1 + temp2 + temp3) / 3);
                })
                .join();
    }

    public static void getFirstTemperature() {
        var future1 = CompletableFuture.supplyAsync(WeatherUtil::getTemperatureFromSource1);
        var future2 = CompletableFuture.supplyAsync(WeatherUtil::getTemperatureFromSource2);
        var future3 = CompletableFuture.supplyAsync(WeatherUtil::getTemperatureFromSource3);

        CompletableFuture.anyOf(future1, future2, future3)
                .exceptionally(th -> {
                    throw new RuntimeException(th);
                })
                .thenAccept(temperature -> System.out.println("Temperature: " + temperature))
                .join();
    }

    public static void getOfferForCustomer() {
        var future1 = CompletableFuture.supplyAsync(CustomerUtil::getCurrentCustomer);
        var future2 = future1.thenApplyAsync(CustomerUtil::getSavingsData);
        var future3 = future1.thenApplyAsync(CustomerUtil::getLoansData);

        var customer = future1
                .exceptionally(th -> { 
                	throw new RuntimeException(th); 
                })
                .join();
        var future = future2
                .thenCombine(future3, ((savings, loans) -> new CustomerDetails(customer, savings, loans)))
                .thenApplyAsync(CustomerUtil::calculateOffer)
                .exceptionally(th -> { 
                	throw new RuntimeException(th); 
                });

        System.out.println("Offer: " + future.join());
    }

}