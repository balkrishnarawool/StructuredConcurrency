package com.balarawool.loom.sc;

import org.junit.jupiter.api.Test;

public class TestBankingPortalSC {
    @Test
    public void getOfferForCustomer() {
        try {
            Thread.startVirtualThread(LoomExamples::getOfferForCustomer).join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
