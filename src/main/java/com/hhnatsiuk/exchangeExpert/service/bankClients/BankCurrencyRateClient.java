package com.hhnatsiuk.exchangeExpert.service.bankClients;

import com.hhnatsiuk.exchangeExpert.model.CurrencyData;

import java.util.List;

public interface BankCurrencyRateClient {

    List<CurrencyData> getCurrentRates();
}
