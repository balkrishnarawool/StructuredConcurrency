package com.balarawool.loom;

public class EventUtil {

    public static Venue reserveVenue() {
        try {
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Venue();
    }

    public static Hotel bookHotel() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Hotel();
    }

    public static Supplies buySupplies() {
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Supplies();
    }

    public record Venue() {}
    public record Hotel() {}
    public record Supplies() {}
    public record Event(Venue venue, Hotel hotel, Supplies supplies) {}
}