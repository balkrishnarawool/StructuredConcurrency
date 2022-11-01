package com.balarawool.loom.util;

import static com.balarawool.loom.util.ThreadUtil.logAndWait;

public class StockUtil {

    public static Price getPriceFromSource1(String stock) {
        logAndWait("getPriceFromSource1", 5);
        return new Price("123.45");
    }

    public static Price getPriceFromSource2(String stock) {
        logAndWait("getPriceFromSource2", 1);
        return new Price("125.67");
    }

    public static Price getPriceFromSource3(String stock) {
        logAndWait("getPriceFromSource3", 5);
        return new Price("127.89");
    }

    public record Price(String price) {}

}