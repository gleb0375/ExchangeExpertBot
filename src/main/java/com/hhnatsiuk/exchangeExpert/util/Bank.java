package com.hhnatsiuk.exchangeExpert.util;

public enum Bank {
    CS("Ceska sporitelna", "CS"),
    CSOB("CSOB", "CSOB"),
    KB("Komercni banka", "KB"),
    RB("Raiffeisenbank", "RB"),
    FB("Fio banka", "FB");

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
        return this.fullName + " - " + this.shortName;
    }

    public static String getAllBanksInfo() {
        StringBuilder builder = new StringBuilder();
        for (Bank bank : Bank.values()) {
            builder.append(bank.toString()).append("\n");
        }
        return builder.toString();
    }
}
