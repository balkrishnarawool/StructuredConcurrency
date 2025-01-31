package com.balarawool.loom.misc_examples.cancellation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CancellationPropagationWithInterruptTest {

    @Test
    void createEvent() {
        var ex = assertThrows(RuntimeException.class, CancellationPropagationWithInterrupt::createEvent);
        assertTrue(ex.getMessage().contains("InterruptedException"));
    }
}