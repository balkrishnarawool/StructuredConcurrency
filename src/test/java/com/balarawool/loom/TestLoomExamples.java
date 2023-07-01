package com.balarawool.loom;

import org.junit.jupiter.api.Test;

public class TestLoomExamples {
    @Test
    public void createEvent() {
        LoomExamples.createEvent();
    }

    @Test
    public void getWeather() {
        LoomExamples.getWeather();
    }

    @Test
    public void getOfferForCustomer() {
        try {
            Thread.startVirtualThread(LoomExamples::getOfferForCustomer).join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
