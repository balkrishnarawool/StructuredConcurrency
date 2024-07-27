package com.balarawool.loom.misc_examples.coord_sched;

import com.balarawool.loom.util.EventUtil;
import com.balarawool.loom.util.EventUtil.Event;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static java.time.temporal.ChronoUnit.SECONDS;

// Example of Coordinated Scheduling.
// To create an event we need all 3 of the following tasks to complete:
// 1. Reserve venue
// 2. Buy supplies
// 3. Book accommodation
// To book accommodation, we need one of the following tasks to complete:
// 3a. Book hotel
// 3b. Book Airbnb
//
public class CoordinatedScheduling {
    public static void createEvent() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var task1 = scope.fork(EventUtil::reserveVenue);
            var task2 = scope.fork(EventUtil::buySupplies);
            var task3 = scope.fork(CoordinatedScheduling::bookAccommodation);

            scope.join().throwIfFailed();

            var venue = task1.get();
            var supplies = task2.get();
            var hotel = task3.get();

            System.out.println(new Event(venue, hotel, supplies));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static EventUtil.Hotel bookAccommodation() {
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<EventUtil.Hotel>()) {
            var task1 = scope.fork(EventUtil::bookHotel);
            var task2 = scope.fork(EventUtil::bookAirbnb);

            var hotel = scope.join().result();

            return hotel;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
