package com.balarawool.loom.util;

import static com.balarawool.loom.util.ThreadUtil.logAndWait;

public class EventUtil {

    public static Venue reserveVenue() {
        logAndWait("reserveVenue", 2);
        return new Venue();
    }

    public static Hotel bookHotel() {
        logAndWait("bookHotel", 1);
        return new Hotel();
    }

    public static Supplies buySupplies() {
        logAndWait("buySupplies", 5);
        return new Supplies();
    }

    public record Venue() {}
    public record Hotel() {}
    public record Supplies() {}
    public record Event(Venue venue, Hotel hotel, Supplies supplies) {}
}