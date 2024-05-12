package com.balarawool.loom.cf;

import com.balarawool.loom.util.EventUtil;
import static com.balarawool.loom.util.EventUtil.Event;

import java.util.concurrent.CompletableFuture;

public class EventCF {
    public static void createEvent() {
        var future1 = CompletableFuture.supplyAsync(EventUtil::reserveVenue);
        var future2 = CompletableFuture.supplyAsync(EventUtil::bookHotel);
        var future3 = CompletableFuture.supplyAsync(EventUtil::buySupplies);

        var futureEvent = CompletableFuture.allOf(future1, future2, future3)
                .exceptionally(th -> {
                    throw new RuntimeException(th);
                })
                .thenApply(_ -> {
                    var venue = future1.join();
                    var hotel = future2.join();
                    var supplies = future3.join();

                    return new Event(venue, hotel, supplies);
                });

        System.out.println("Event : " + futureEvent.join());
    }
}