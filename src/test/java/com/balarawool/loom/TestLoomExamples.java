package com.balarawool.loom;

import org.junit.jupiter.api.Test;

public class TestLoomExamples {
    @Test
    public void getAverageTemperature() {
        LoomExamples.getAverageTemperature();
    }

    @Test
    public void getFirstTemperature() {
        LoomExamples.getFirstTemperature();
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
