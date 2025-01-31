package com.balarawool.loom.misc_examples.cancellation;

import org.junit.jupiter.api.Test;

import java.util.concurrent.StructuredTaskScope.FailedException;

import static org.junit.jupiter.api.Assertions.*;

class CancellationPropagationWithErrorTest {

    @Test
    void createEvent() {
        var ex = assertThrows(FailedException.class, CancellationPropagationWithError::createEvent);
        assertTrue(ex.getMessage().contains("Error while buying supplies"));
    }

}