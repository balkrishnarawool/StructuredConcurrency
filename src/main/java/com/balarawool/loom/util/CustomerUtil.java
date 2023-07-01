package com.balarawool.loom.util;

import static com.balarawool.loom.util.ThreadUtil.logAndWait;

public class CustomerUtil {

    public static Customer getCurrentCustomer() {
        logAndWait("getCurrentCustomer");
        return new Customer();
    }

    public static Savings getSavingsData(Customer customer) {
        logAndWait("getSavingsData");
        return new Savings(customer);
    }

    public static Loans getLoansData(Customer customer) {
        logAndWait("getLoansData");
        return new Loans(customer);
    }

    public static Offer calculateOffer(CustomerDetails customerDetails) {
        logAndWait("calculateOffer");
        return new Offer(customerDetails);
    }

    public record Customer() {}
    public record Savings(Customer customer) {}
    public record Loans(Customer customer) {}
    public record CustomerDetails(Customer customer, Savings savings, Loans loans) {}
    public record Offer(CustomerDetails customerDetails) {}
}
