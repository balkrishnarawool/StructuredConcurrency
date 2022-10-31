package com.balarawool.loom;

import org.junit.jupiter.api.Test;

public class TestLoom {

    @Test
    public void sequence() {
        try {
            Thread.startVirtualThread(LoomExamples::sequence).join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void allOf() {
        LoomExamples.allOf();
    }

    @Test
    public void anyOf() {
        LoomExamples.anyOf();
    }

    @Test
    public void thenCombine() {
        try {
            Thread.startVirtualThread(LoomExamples::thenCombine).join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
