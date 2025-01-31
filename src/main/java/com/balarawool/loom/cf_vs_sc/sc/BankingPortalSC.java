package com.balarawool.loom.cf_vs_sc.sc;

import com.balarawool.loom.util.CustomerUtil;
import com.balarawool.loom.util.CustomerUtil.CustomerDetails;

import java.util.concurrent.StructuredTaskScope;

public class BankingPortalSC {
    public static void getOfferForCustomer() {
        var customer = CustomerUtil.getCurrentCustomer();
        try (var scope = StructuredTaskScope.open()) {
            var task1 = scope.fork(() -> CustomerUtil.getSavingsData(customer));
            var task2 = scope.fork(() -> CustomerUtil.getLoansData(customer));

            scope.join();

            var savings = task1.get();
            var loans = task2.get();
            var customerDetails = new CustomerDetails(customer, savings, loans);

            var offer = CustomerUtil.calculateOffer(customerDetails);
            System.out.println(offer);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
