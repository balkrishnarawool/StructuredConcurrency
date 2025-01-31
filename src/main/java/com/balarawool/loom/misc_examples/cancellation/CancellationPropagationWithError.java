package com.balarawool.loom.misc_examples.cancellation;

import com.balarawool.loom.util.EventUtil;
import com.balarawool.loom.util.EventUtil.Event;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

// Example of cancellation propagation: When a task in a ShutdownOnFailure scope is cancelled due to an exception (RuntimeException), all other tasks are cancelled and all child-scopes are cancelled as well.
// To create an event we need all 3 of the following tasks to complete:
// 1. Reserve venue
// 2. Buy supplies
// 3. Book accommodation
// To book accommodation, we need one of the following tasks to complete:
// 3a. Book hotel
// 3b. Book Airbnb
// In the example below, buySuppliesWithError() throws an exception which then cancels the scope it is in (cancelling all uncompleted tasks in it) and
// it also cancels nested scope (cancelling all uncompleted tasks in it).
//
// TODO: Show that the other tasks and child-scope-tasks are cancelled as well. Perhaps add a println() in a method which is executed when a task is cancelled.
// Think about other places where such println()-s could be useful.
// Also, capture the exception and where it occurred.
//
public class CancellationPropagationWithError {
    public static void createEvent() {
        try (var scope = StructuredTaskScope.open()) {
            var task1 = scope.fork(EventUtil::reserveVenue);
            var task2 = scope.fork(EventUtil::buySuppliesWithError);
            var task3 = scope.fork(CancellationPropagationWithError::bookAccommodation);

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
