package com.balarawool.loom;

import com.balarawool.loom.util.CustomerUtil;
import com.balarawool.loom.util.CustomerUtil.CustomerDetails;
import com.balarawool.loom.util.EventUtil;
import com.balarawool.loom.util.WeatherUtil;
import com.balarawool.loom.util.WeatherUtil.Weather;

import java.util.concurrent.CompletableFuture;
public class CFExamples {
    public static void createEvent() {
        var future1 = CompletableFuture.supplyAsync(EventUtil::reserveVenue);
        var future2 = CompletableFuture.supplyAsync(EventUtil::bookHotel);
        var future3 = CompletableFuture.supplyAsync(EventUtil::buySupplies);

        var futureEvent = CompletableFuture.allOf(future1, future2, future3)
                .exceptionally(th -> {
                    throw new RuntimeException(th);
                })
                .thenApply(ignored -> {
                    var venue = future1.join();
                    var hotel = future2.join();
                    var supplies = future3.join();

                    return new EventUtil.Event(venue, hotel, supplies);
                });

        System.out.println("Event : " + futureEvent.join());
    }

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