package com.balarawool.loom.cf;

import com.balarawool.loom.util.CustomerUtil;
import com.balarawool.loom.util.CustomerUtil.CustomerDetails;

import java.util.concurrent.CompletableFuture;

public class BankingPortalCF {
    public static void getOfferForCustomer() {
        var future1 = CompletableFuture.supplyAsync(CustomerUtil::getCurrentCustomer);
        var future2 = future1.thenApplyAsync(CustomerUtil::getSavingsData);
        var future3 = future1.thenApplyAsync(CustomerUtil::getLoansData);

        var customer = future1
                .exceptionally(th -> { 
                	throw new RuntimeException(th); 
                })
                .join();
        var future = future2
                .thenCombine(future3, ((savings, loans) -> new CustomerDetails(customer, savings, loans)))
                .thenApplyAsync(CustomerUtil::calculateOffer)
                .exceptionally(th -> { 
                	throw new RuntimeException(th); 
                });

        System.out.println("Offer: " + future.join());
    }
}