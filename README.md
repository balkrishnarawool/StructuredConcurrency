# Structured Concurrency in Java
This repo provides examples for
- Comparision between CompletableFuture API and Structured Concurrency API
- Implementing miscellaneous scenarios with Structured Concurrency API
  
# CompletableFuture vs. Structured Concurrency API

``CompletableFuture`` provides API for asynchronous processing. It is qiite a powerful API and using it, you can chain multiple "stages" to create a pipeline. It relies heavily on callbacks, as the stages are executed asynchronously.

Structured Concurrency, on the other hand, provides an API that makes code imperative. 

Examples for these are present in package `com.balarawool.loom.cf_vs_sc`.
This is part of my talk [Structured Concurrency in Java: The what and the why](https://www.youtube.com/watch?v=fbI3qveS_Is). The examples discussed in the talk are implemented here. If you have any questions/ feedback, do reach me out [@BalaRawool](https://twitter.com/@BalaRawool).

There are 3 examples: Event management, Weather service and Banking portal
The class ``CFExamples`` implements these examples using ``CompletableFuture`` and ``LoomExamples`` class implements then using structured concurrency API.

## Example 1: Event management
Execute all sub tasks.
With ``CompletableFuture``, it looks like this:

    public static void createEvent() {
        var future1 = CompletableFuture.supplyAsync(EventUtil::reserveVenue);
        var future2 = CompletableFuture.supplyAsync(EventUtil::bookHotel);
        var future3 = CompletableFuture.supplyAsync(EventUtil::buySupplies);

        var futureEvent = CompletableFuture.allOf(future1, future2, future3)
                .thenApply(ignored -> {
                    var venue = future1.join();
                    var hotel = future2.join();
                    var supplies = future3.join();

                    return new EventUtil.Event(venue, hotel, supplies);
                });

        System.out.println("Event : " + futureEvent.join());
    }

Equivalent code with structured concurrency is:

    public static void createEvent() {
        try(var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var task1 = scope.fork(EventUtil::reserveVenue);
            var task2 = scope.fork(EventUtil::bookHotel);
            var task3 = scope.fork(EventUtil::buySupplies);

            scope.join();

            var venue = task1.get();
            var hotel = task2.get();
            var supplies = task3.get();

            System.out.println("Event: " + new EventUtil.Event(venue, hotel, supplies));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


## Example 2: Weather service
Execute at least one sub task:

With ``CompletableFuture``, it looks like this:

    public static void getWeather() {
        var future1 = CompletableFuture.supplyAsync(() -> WeatherUtil.getWeatherFromSource1("Amsterdam"));
        var future2 = CompletableFuture.supplyAsync(() -> WeatherUtil.getWeatherFromSource2("Amsterdam"));
        var future3 = CompletableFuture.supplyAsync(() -> WeatherUtil.getWeatherFromSource3("Amsterdam"));

        CompletableFuture.anyOf(future1, future2, future3)
                .exceptionally(th -> {
                    throw new RuntimeException(th);
                })
                .thenAccept(weather -> System.out.println("Weather: " + weather))
                .join();
    }

And with structured concurrency:

    public static void getWeather() {
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<WeatherUtil.Weather>()) {
            scope.fork(() -> WeatherUtil.getWeatherFromSource1("Amsterdam"));
            scope.fork(() -> WeatherUtil.getWeatherFromSource2("Amsterdam"));
            scope.fork(() -> WeatherUtil.getWeatherFromSource3("Amsterdam"));

            var weather = scope.join().result();
            System.out.println(weather);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

## Example 3: Banking portal
Combine multiple sub tasks:

With ``CompletableFuture``, it looks like this:

    public static void getOfferForCustomer() {
        var future1 = CompletableFuture.supplyAsync(CustomerUtil::getCurrentCustomer);
        var future2 = future1.thenApplyAsync(CustomerUtil::getSavingsData);
        var future3 = future1.thenApplyAsync(CustomerUtil::getLoansData);

        var customer = future1
                .exceptionally(th -> { throw new RuntimeException(th); })
                .join();
        var future = future2
                .thenCombine(future3, ((savings, loans) -> new CustomerDetails(customer, savings, loans)))
                .thenApplyAsync(CustomerUtil::calculateOffer)
                .exceptionally(th -> { throw new RuntimeException(th); });

        System.out.println("Offer: " + future.join());
    }

And with structured concurrency:

    public static void getOfferForCustomer() {
        var customer = CustomerUtil.getCurrentCustomer();

        try(var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var task1 = scope.fork(() -> CustomerUtil.getSavingsData(customer));
            var task2 = scope.fork(() -> CustomerUtil.getLoansData(customer));

            scope.join().throwIfFailed();
            var savings = task1.get();
            var loans = task2.get();

            var details = new CustomerUtil.CustomerDetails(customer, savings, loans);

            var offer = CustomerUtil.calculateOffer(details);
            System.out.println(offer);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

There are two main differences:
- ``CompletableFuture`` uses asynchronous mechanism and callbacks to implement pipelines. Whereas structured concurrency makes code imperative. Because of this, the code with structured concurrency is often more readable than the one with ``CompletableFuture``.
- Also, ``CompletableFuture`` relies on lambdas which means we can only throw ``RuntimeException``-s. Structured concurrency uses checked exceptions. ``CompletionException`` used by ``CompletableFuture`` is ``RuntimeException``. Whereas ``ExecutionException`` used by structured concurrency is checked exception. This means with structured concurrency, you are forced to catch these exceptions ans handle the error scenarios.

Besides these differences, we see some peculiar situations and quirks of ``CompletableFuture`` and structured concurrency API during the talk.


# Implementing miscellaneous scenarios with Structured Concurrency API
I have implemented few examples using Structured Concurrency API which deal with:
- coordinated scheduling
- cancellations
- timeouts
- graceful shutdown

These examples are present in the package `com.balarawool.loom.misc_examples`. I'll write more about them later.
There are still things todo in those examples.

## Pre-requisites:

- Make sure to use JDK 21 or higher.

- Make sure you use ``--enable-preview`` for build and run.
If you are using IntelliJ:
    - Make sure to enable preview for Java Compiler:
    - Make sure to enbale preview in 'Run Configurations...'
