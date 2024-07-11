package com.hhnatsiuk.exchangeExpert.util;

public enum Currency {
    USD("USD"),
    EUR("EUR"),
    CZK("CZK");

    private final String description;

    Currency(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
