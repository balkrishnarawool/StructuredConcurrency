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

    public record Venue() {}
    public record Hotel() {}
    public record Supplies() {}
    public record Event(Venue venue, Hotel hotel, Supplies supplies) {}
}