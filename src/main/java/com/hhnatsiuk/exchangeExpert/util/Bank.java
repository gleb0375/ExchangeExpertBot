package com.hhnatsiuk.exchangeExpert.util;

public enum Bank {
    CSOB("CSOB", "csob"),
    KB("Komercni banka", "kb"),
    RB("Raiffeisenbank", "rb"),
    FB("Fio banka", "fb");

    private final String fullName;
    private final String shortName;

    Bank(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return this.fullName + " - " + this.shortName.toUpperCase();
    }

    public static String getAllBanksInfo() {
        StringBuilder builder = new StringBuilder();
        for (Bank bank : Bank.values()) {
            builder.append(bank.toString()).append("\n");
        }
        return builder.toString();
    }

    public static Bank getByShortName(String shortName) {
        for (Bank bank : values()) {
            if (bank.getShortName().equalsIgnoreCase(shortName)) {
                return bank;
            }
        }
        return null;
    }
}
