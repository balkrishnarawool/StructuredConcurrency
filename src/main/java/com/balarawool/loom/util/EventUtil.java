package com.balarawool.loom.util;

import static com.balarawool.loom.util.ThreadUtil.logAndWait;

public class EventUtil {

    public static Venue reserveVenue() {
        logAndWait("reserveVenue");
        return new Venue();
    }

    public static Hotel bookHotel() {
        logAndWait("bookHotel");
        return new Hotel();
    }

    public static Supplies buySupplies() {
        logAndWait("buySupplies");
        return new Supplies();
    }

    public static Supplies buySuppliesWithError() {
        int delay = (int) (Math.random() * 5);
        try {
            Thread.sleep(delay * 1_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Error while buying supplies");
    }

    public static Hotel bookAirbnb() {
        logAndWait("bookAirbnb");
        return new Hotel();
    }

    public record Venue() {}
    public record Hotel() {}
    public record Supplies() {}
    public record Event(Venue venue, Hotel hotel, Supplies supplies) {}
}