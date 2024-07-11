package com.hhnatsiuk.exchangeExpert.util;

public enum OperationType {
    BUY("Buy"),
    SELL("Sell");

    private final String description;

    OperationType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
