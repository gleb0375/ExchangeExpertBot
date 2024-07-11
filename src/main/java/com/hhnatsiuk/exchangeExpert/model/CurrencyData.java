package com.hhnatsiuk.exchangeExpert.model;

public class CurrencyData {

    private String currency;
    private double buyRate;
    private double sellRate;
    private String bankName;

    public CurrencyData(String currency, double buyRate, double sellRate, String bankName) {
        this.currency = currency;
        this.buyRate = buyRate;
        this.sellRate = sellRate;
        this.bankName = bankName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getBuyRate() {
        return buyRate;
    }

    public void setBuyRate(double buyRate) {
        this.buyRate = buyRate;
    }

    public double getSellRate() {
        return sellRate;
    }

    public void setSellRate(double sellRate) {
        this.sellRate = sellRate;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
