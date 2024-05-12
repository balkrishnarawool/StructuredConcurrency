package com.balarawool.loom.sc;

import com.balarawool.loom.util.EventUtil;
import com.balarawool.loom.util.EventUtil.Event;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public class EventSC {
    public static void createEvent() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var task1 = scope.fork(EventUtil::reserveVenue);
            var task2 = scope.fork(EventUtil::bookHotel);
            var task3 = scope.fork(EventUtil::buySupplies);

            scope.join().throwIfFailed();

            var venue = task1.get();
            var hotel = task2.get();
            var supplies = task3.get();

            System.out.println(new Event(venue, hotel, supplies));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
