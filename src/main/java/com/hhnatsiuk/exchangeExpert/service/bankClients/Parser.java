package com.hhnatsiuk.exchangeExpert.service.bankClients;

import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import com.hhnatsiuk.exchangeExpert.util.Currency;

import java.util.Collections;
import java.util.List;

public interface Parser {

    String USD = Currency.USD.toString();
    String EUR = Currency.EUR.toString();

    static List<CurrencyData> parse(String response, String bankName) {
        return Collections.emptyList();
    }
}
