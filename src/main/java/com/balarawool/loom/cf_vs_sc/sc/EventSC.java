package com.balarawool.loom.cf_vs_sc.sc;

import com.balarawool.loom.util.EventUtil;
import com.balarawool.loom.util.EventUtil.Event;

import java.util.concurrent.StructuredTaskScope;

public class EventSC {
    public static void createEvent() {
        try (var scope = StructuredTaskScope.open()) {
            var task1 = scope.fork(EventUtil::reserveVenue);
            var task2 = scope.fork(EventUtil::bookHotel);
            var task3 = scope.fork(EventUtil::buySupplies);

            scope.join();

            var venue = task1.get();
            var hotel = task2.get();
            var supplies = task3.get();

            System.out.println(new Event(venue, hotel, supplies));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
