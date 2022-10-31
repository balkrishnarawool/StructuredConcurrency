package com.balarawool.loom;

public class StockUtil {

    public static Price getPriceFromSource1(String stock) {
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Price("123.45");
    }

    public static Price getPriceFromSource2(String stock) {
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Price("125.67");
    }

    public static Price getPriceFromSource3(String stock) {
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Price("127.89");
    }

    public record Price(String price) {}

}