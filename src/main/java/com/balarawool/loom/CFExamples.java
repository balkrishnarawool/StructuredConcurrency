package com.balarawool.loom;

import java.util.concurrent.CompletableFuture;

import com.balarawool.loom.CustomerUtil.CustomerDetails;

public class CFExamples {

    public static void sequence() {
        var future = CompletableFuture.supplyAsync(GamesUtil::getPlayer)
                .thenApply(GamesUtil.Player::performance)
                .thenApply(GamesUtil.Performance::scores)
                .thenApply(scores -> scores.stream().map(GamesUtil.Score::runs).filter(i -> i >= 100).count());

        System.out.println("Centuries: " + future.join());
    }

    public static void allOf() {
        var future1 = CompletableFuture.supplyAsync(EventUtil::reserveVenue);
        var future2 = CompletableFuture.supplyAsync(EventUtil::bookHotel);
        var future3 = CompletableFuture.supplyAsync(EventUtil::buySupplies);

        var futureEvent = CompletableFuture.allOf(future1, future2, future3)
                .thenApply(ignored -> {
                    var venue = future1.join();
                    var hotel = future2.join();
                    var supplies = future3.join();

                    return new EventUtil.Event(venue, hotel, supplies);
                });

        System.out.println("Event : " + futureEvent.join());
    }

    public static void anyOf() {
        var future1 = CompletableFuture.supplyAsync(() -> StockUtil.getPriceFromSource1("APPL"));
        var future2 = CompletableFuture.supplyAsync(() -> StockUtil.getPriceFromSource2("APPL"));
        var future3 = CompletableFuture.supplyAsync(() -> StockUtil.getPriceFromSource3("APPL"));

        CompletableFuture.anyOf(future1, future2, future3)
                .thenAccept(price -> System.out.println("Price: " + price))
                .join();
    }

    public static void thenCombine() {
        var future1 = CompletableFuture.supplyAsync(CustomerUtil::getCurrentCustomer);
        var future2 = future1.thenApply(CustomerUtil::getSavingsData);
        var future3 = future1.thenApply(CustomerUtil::getLoansData);

        var future = future2.thenCombine(future3, (savings, loans) -> new CustomerDetails(savings, loans));

        System.out.println("Customer details: " + future.join());
    }
}