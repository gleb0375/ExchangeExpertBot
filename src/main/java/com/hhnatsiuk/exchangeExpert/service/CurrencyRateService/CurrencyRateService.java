package com.hhnatsiuk.exchangeExpert.service.CurrencyRateService;

import com.hhnatsiuk.exchangeExpert.model.CurrencyData;

import java.util.List;

public interface CurrencyRateService {
    List<CurrencyData> getCurrentRates(String BankName);
}
