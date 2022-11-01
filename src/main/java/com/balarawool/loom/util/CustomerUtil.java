package com.balarawool.loom.util;

import static com.balarawool.loom.util.ThreadUtil.logAndWait;

public class CustomerUtil {

    public static Customer getCurrentCustomer() {
        logAndWait("getCurrentCustomer", 2);
        return new Customer();
    }

    public static Savings getSavingsData(Customer customer) {
        logAndWait("getSavingsData", 2);
        return new Savings(customer);
    }

    public static Loans getLoansData(Customer customer) {
        logAndWait("getLoansData", 2);
        return new Loans(customer);
    }
    public record Customer() {}
    public record Savings(Customer customer) {}
    public record Loans(Customer customer) {}
    public record CustomerDetails(Savings savings, Loans loans) {}
}
