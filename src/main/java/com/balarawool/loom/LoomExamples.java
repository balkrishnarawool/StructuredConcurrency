package com.balarawool.loom;

import jdk.incubator.concurrent.StructuredTaskScope;

public class LoomExamples {

    public static void sequence() {
        var player = GamesUtil.getPlayer();
        var centuries = player.performance()
                .scores()
                .stream()
                .map(GamesUtil.Score::runs)
                .filter(i -> i >= 100)
                .count();
        System.out.println("Centuries: " + centuries);
    }

    public static void allOf() {
        try(var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future1 = scope.fork(EventUtil::reserveVenue);
            var future2 = scope.fork(EventUtil::bookHotel);
            var future3 = scope.fork(EventUtil::buySupplies);

            scope.join();

            var venue = future1.resultNow();
            var hotel = future2.resultNow();
            var supplies = future3.resultNow();

            System.out.println("Event: " + new EventUtil.Event(venue, hotel, supplies));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void anyOf() {
        try(var scope = new StructuredTaskScope.ShutdownOnSuccess<StockUtil.Price>()) {
            scope.fork(() -> StockUtil.getPriceFromSource1("APPL"));
            scope.fork(() -> StockUtil.getPriceFromSource2("APPL"));
            scope.fork(() -> StockUtil.getPriceFromSource3("APPL"));

            var price = scope.join().result(RuntimeException::new);

            System.out.println("Price: "+ price);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void thenCombine() {
        var customer = CustomerUtil.getCurrentCustomer();

        try(var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future2 = scope.fork(() -> CustomerUtil.getSavingsData(customer));
            var future3 = scope.fork(() -> CustomerUtil.getLoansData(customer));

            scope.join();

            var savings = future2.resultNow();
            var loans = future3.resultNow();

            System.out.println("Customer details: " + new CustomerUtil.CustomerDetails(savings, loans));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
