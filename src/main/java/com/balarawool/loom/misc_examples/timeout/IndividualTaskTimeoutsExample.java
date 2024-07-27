package com.balarawool.loom.misc_examples.timeout;

import com.balarawool.loom.util.EventUtil;
import com.balarawool.loom.util.EventUtil.Event;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static java.time.temporal.ChronoUnit.SECONDS;

// Example of individual task timeouts.
// This works but not exactly as expected.
// TODO: Sometimes it performs the task and then throws TimeoutException for it. It should throw the exception when timeout occurs, so before completing the task.
// TODO: Identify which task gets exception.
// TODO: On successful completion, close() method throws an exception. Find out why.
// TODO: We get TimeoutException from only one of the tasks. All tasks that timed-out should throw this exception.

public class IndividualTaskTimeoutsExample {
    public static void createEvent() {
        try (var scope = new ShutdownOnFailureWithTimeouts()) {
            Supplier<EventUtil.Venue> task1 = scope.fork(EventUtil::reserveVenue, Time.after(5, SECONDS));
            Supplier<EventUtil.Hotel> task2 = scope.fork(EventUtil::bookHotel, Time.after(6, SECONDS));
            Supplier<EventUtil.Supplies> task3 = scope.fork(EventUtil::buySupplies, Time.after(4, SECONDS));

            scope.joinUntil(Instant.now().plus(8, SECONDS));

            var venue = task1.get();
            var hotel = task2.get();
            var supplies = task3.get();

            System.out.println(new Event(venue, hotel, supplies));
        } catch (InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ShutdownOnFailureWithTimeouts<U> extends StructuredTaskScope<U> implements AutoCloseable {
        record ChildScope(StructuredTaskScope scope, Instant timeout) { }
        List<ChildScope> childScopes = new ArrayList<>();

        public <U1> Supplier<U1> fork(Callable<U1> task, Instant instant) {
            var scope = new StructuredTaskScope<U1>();
            childScopes.add(new ChildScope(scope, instant));
            var subTask = scope.fork(task::call);
            return subTask::get;
        }

        @Override
        public ShutdownOnFailureWithTimeouts<U> joinUntil(Instant deadline) throws InterruptedException, TimeoutException {
            for (var scope: childScopes) { scope.scope.joinUntil(scope.timeout); }
            super.joinUntil(deadline);
            return this;
        }

        @Override
        public void close() {
//            for (var scope: childScopes) { scope.scope.close(); } // This throws exception (on successful completion). Find out why.
//            super.close(); // This throws exception (on successful completion). Find out why.
        }
    }

    private static class Time {
        private static Instant after(int duration, ChronoUnit unit) {
            return Instant.now().plus(duration, unit);
        }
    }
}
