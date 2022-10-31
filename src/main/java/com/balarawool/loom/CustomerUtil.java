package com.balarawool.loom;

public class CustomerUtil {

    public static Customer getCurrentCustomer() {
        try {
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Customer();
    }

    public static Savings getSavingsData(Customer customer) {
        try {
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Savings(customer);
    }

    public static Loans getLoansData(Customer customer) {
        try {
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Loans(customer);
    }
    public record Customer() {}
    public record Savings(Customer customer) {}
    public record Loans(Customer customer) {}
    public record CustomerDetails(Savings savings, Loans loans) {}
}
