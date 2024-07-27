package com.balarawool.loom.sc;

import org.junit.jupiter.api.Test;

public class TestGracefulShutdown {
    @Test
    public void doShutdown() {
        GracefulShutdown.doShutdown();
    }

    @Test
    public void doGracefulShutdown() {
        // This works. (From platform thread.)
        GracefulShutdown.doGracefulShutdown();

        // This doesn't work. (From virtual thread.)
        // Problem is: When a ShutdownHook is added, the resources close immediately and the application coses, even though there are Thread.sleep() statements.
        // When ShutdownHook is removed, nothing gets printed, the application closes immediately.
        // The problem could be with custom shutdown policy GracefulShutdownScope or
        // TODO: Fix the problem
//        Thread.startVirtualThread(() -> GracefulShutdown.doGracefulShutdown());
    }
}
