package com.hhnatsiuk.exchangeExpert.service.CurrencyRateService;

import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import com.hhnatsiuk.exchangeExpert.service.bankClients.BankCurrencyRateClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyRateServiceImpl implements CurrencyRateService {
    private final BankCurrencyRateClientFactory clientFactory;

    @Autowired
    public CurrencyRateServiceImpl(BankCurrencyRateClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public List<CurrencyData> getCurrentRates(String bankName) {
        List<CurrencyData> rates = clientFactory.getClient(bankName).getCurrentRates();
        return sortRates(rates);
    }


    private List<CurrencyData> sortRates(List<CurrencyData> rates) {
        List<String> currencyOrder = List.of("EUR", "USD");
        return rates.stream()
                .sorted(Comparator.comparingInt(rate -> currencyOrder.indexOf(rate.getCurrency())))
                .collect(Collectors.toList());
    }
}
