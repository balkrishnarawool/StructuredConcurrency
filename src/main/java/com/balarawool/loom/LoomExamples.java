package com.balarawool.loom;

import com.balarawool.loom.util.CustomerUtil;
import com.balarawool.loom.util.CustomerUtil.CustomerDetails;
import com.balarawool.loom.util.EventUtil;
import com.balarawool.loom.util.EventUtil.Event;
import com.balarawool.loom.util.WeatherUtil;
import com.balarawool.loom.util.WeatherUtil.Weather;

import java.util.concurrent.StructuredTaskScope;

import java.util.concurrent.ExecutionException;

public class LoomExamples {
    public static void createEvent() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var task1 = scope.fork(EventUtil::reserveVenue);
            var task2 = scope.fork(EventUtil::bookHotel);
            var task3 = scope.fork(EventUtil::buySupplies);

            scope.join().throwIfFailed();

            var venue = task1.get();
            var hotel = task2.get();
            var supplies = task3.get();

            System.out.println(new Event(venue, hotel, supplies));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

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

    public static void getOfferForCustomer() {
    	var customer = CustomerUtil.getCurrentCustomer();
    	
    	try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    		var task1 = scope.fork(() -> CustomerUtil.getSavingsData(customer));
    		var task2 = scope.fork(() -> CustomerUtil.getLoansData(customer));
    		
    		scope.join().throwIfFailed();
    		
    		var savings = task1.get();
    		var loaans = task2.get();
    		var customerDetails = new CustomerDetails(customer, savings, loaans);
    		
    		var offer = CustomerUtil.calculateOffer(customerDetails);
    		
    		System.out.println(offer);
    	} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
    }
}
