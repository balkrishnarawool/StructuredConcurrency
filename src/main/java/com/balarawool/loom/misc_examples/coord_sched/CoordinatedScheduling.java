package com.balarawool.loom.misc_examples.coord_sched;

import com.balarawool.loom.util.EventUtil;
import com.balarawool.loom.util.EventUtil.Event;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

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
        try (var scope = StructuredTaskScope.open()) {
            var task1 = scope.fork(EventUtil::reserveVenue);
            var task2 = scope.fork(EventUtil::buySupplies);
            var task3 = scope.fork(CoordinatedScheduling::bookAccommodation);

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
