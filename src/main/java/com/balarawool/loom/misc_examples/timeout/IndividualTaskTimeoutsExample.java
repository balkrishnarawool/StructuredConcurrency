package com.balarawool.loom.misc_examples.timeout;

import com.balarawool.loom.util.EventUtil;
import com.balarawool.loom.util.EventUtil.Event;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Config;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.function.Function;
import java.util.function.Supplier;

// Example of individual task timeouts.
// This works but not exactly as expected.
// TODO: Sometimes it performs the task and then throws TimeoutException for it. It should throw the exception when timeout occurs, so before completing the task.
// TODO: Identify which task gets exception.
// TODO: On successful completion, close() method throws an exception. Find out why.
// TODO: We get TimeoutException from only one of the tasks. All tasks that timed-out should throw this exception.

public class IndividualTaskTimeoutsExample {
    public static void createEvent() {
        try (var scope = ScopeWithTaskTimeouts.open(Joiner.allSuccessfulOrThrow(), config -> config.withTimeout(Duration.ofSeconds(20)))) {
            Supplier<EventUtil.Venue> task1 = scope.fork(EventUtil::reserveVenue, Duration.ofSeconds(5));
            Supplier<EventUtil.Hotel> task2 = scope.fork(EventUtil::bookHotel, Duration.ofSeconds(6));
            Supplier<EventUtil.Supplies> task3 = scope.fork(EventUtil::buySupplies, Duration.ofSeconds(4));

            scope.join();

            var venue = task1.get();
            var hotel = task2.get();
            var supplies = task3.get();

            System.out.println(new Event(venue, hotel, supplies));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ScopeWithTaskTimeouts<T, R> implements AutoCloseable {
        private StructuredTaskScope<T, R> scope;
        private List<StructuredTaskScope> childScopes;

        private ScopeWithTaskTimeouts(Joiner joiner, Function<Config, Config> configFunction) {
            scope = StructuredTaskScope.open(joiner, configFunction);
            childScopes = new ArrayList<>();
        }

        public static ScopeWithTaskTimeouts open(Joiner joiner, Function<Config, Config> configFunction) {
            return new ScopeWithTaskTimeouts(joiner, configFunction);
        }

        public StructuredTaskScope.Subtask<T> fork(Callable<T> task, Duration timeout) {
            var tempScope = StructuredTaskScope.open(Joiner.allSuccessfulOrThrow(), config -> config.withTimeout(timeout));
            System.out.println(tempScope);
            childScopes.add(tempScope);
            return tempScope.fork(task::call);
        }

        // TODO: BIG PROBLEM: The outer scope always throws TimeoutException even if all child scopes are successfully finished/joined and scope has still time.
        // TODO: When scope's timeout is lower than one of child's timeout, it might not throw TimeoutException in time.
        public R join() throws InterruptedException {
            childScopes.stream().forEach(s -> {
                try {
                    System.out.println(s);
                    s.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            return scope.join();
        }

        @Override
        public void close() {
            scope.close();
            // TODO Fix these:
    //            for (var scope: childScopes) { scope.scope.close(); } // This throws exception (on successful completion). Find out why.
    //            super.close(); // This throws exception (on successful completion). Find out why.
        }
    }
}
