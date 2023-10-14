package com.balarawool.loom;

import com.balarawool.loom.util.CustomerUtil;
import com.balarawool.loom.util.EventUtil;
import com.balarawool.loom.util.WeatherUtil;
import com.balarawool.loom.util.WeatherUtil.Weather;

import java.util.concurrent.StructuredTaskScope;

import java.util.concurrent.ExecutionException;

import static com.balarawool.loom.util.CustomerUtil.CustomerDetails;

public class LoomExamples {
	public static void getAverageTemperature() {
		try(var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			var task1 = scope.fork(WeatherUtil::getTemperatureFromSource1);
			var task2 = scope.fork(WeatherUtil::getTemperatureFromSource2);
			var task3 = scope.fork(WeatherUtil::getTemperatureFromSource3);
			
			scope.join().throwIfFailed();
			
			var temp1 = task1.get();
			var temp2 = task2.get();
			var temp3 = task3.get();
			
        	System.out.println("Average temperature: " + (double)(temp1 + temp2 + temp3) / 3);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
    }

    public static void getFirstTemperature() {
		try(var scope = new StructuredTaskScope.ShutdownOnSuccess<Integer>()) {
			scope.fork(WeatherUtil::getTemperatureFromSource1);
			scope.fork(WeatherUtil::getTemperatureFromSource2);
			scope.fork(WeatherUtil::getTemperatureFromSource3);
			
			var temp = scope.join().result();
			
        	System.out.println(temp);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
    }

    public static void getOfferForCustomer() {
    	var customer = CustomerUtil.getCurrentCustomer();
    	
    	try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    		var task1 = scope.fork(() -> CustomerUtil.getSavingsData(customer));
    		var task2 = scope.fork(() -> CustomerUtil.getLoansData(customer));
    		
    		scope.join().throwIfFailed();
    		
    		var savings = task1.get();
    		var loans = task2.get();
    		
    		var details = new CustomerDetails(customer, savings, loans);
    		
    		var offer = CustomerUtil.calculateOffer(details);
    		System.out.println(offer);
    	} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
    }
}
