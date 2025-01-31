package com.balarawool.loom.misc_examples.cancellation;

import com.balarawool.loom.util.EventUtil;
import com.balarawool.loom.util.EventUtil.Event;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

// Example of cancellation propagation. One where thread of scope is interrupted and this interruption cancels the scope which is propagated to child threads.
// To create an event we need all 3 of the following tasks to complete:
// 1. Reserve venue
// 2. Buy supplies
// 3. Book accommodation
// To book accommodation, we need one of the following tasks to complete:
// 3a. Book hotel
// 3b. Book Airbnb
// In the example below, the main thread is interrupted which then cancels the scope, (which was created in main thread) cancelling all uncompleted tasks in it and
// it also cancels nested scope (cancelling all uncompleted tasks in it).
//
// TODO: Show that the other tasks and child-scope-tasks are cancelled as well. Perhaps add a println() in a method which is executed when a task is cancelled.
// Think about other places where such println()-s could be useful.
// Also, capture the exception and where it occurred.
//
public class CancellationPropagationWithInterrupt {
    public static void createEvent() {
        var t = Thread.currentThread();
        new Thread(() -> {
            try {
                Thread.sleep(5 * 1000);
                t.interrupt();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        try (var scope = StructuredTaskScope.open()) {
            var task1 = scope.fork(EventUtil::reserveVenue);
            var task2 = scope.fork(EventUtil::buySupplies);
            var task3 = scope.fork(CancellationPropagationWithInterrupt::bookAccommodation);

            scope.join();

            var venue = task1.get();
            var supplies = task2.get();
            var hotel = task3.get();

            System.out.println(new Event(venue, hotel, supplies));
        } catch (InterruptedException e) {
             throw new RuntimeException(e);
        }
    }

    private static EventUtil.Hotel bookAccommodation() {
        try (var scope = StructuredTaskScope.open(Joiner.<EventUtil.Hotel>anySuccessfulResultOrThrow())) {
            var task1 = scope.fork(EventUtil::bookHotel);
            var task2 = scope.fork(EventUtil::bookAirbnb);

            var hotel = scope.join();

            return hotel;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
