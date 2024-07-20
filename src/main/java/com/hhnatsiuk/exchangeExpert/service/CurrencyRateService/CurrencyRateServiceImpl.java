package com.hhnatsiuk.exchangeExpert.service.CurrencyRateService;

import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import com.hhnatsiuk.exchangeExpert.service.bankClients.BankCurrencyRateClientFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyRateServiceImpl implements CurrencyRateService {
    private static final Logger log = LogManager.getLogger(CurrencyRateServiceImpl.class);

    private final BankCurrencyRateClientFactory clientFactory;

    @Autowired
    public CurrencyRateServiceImpl(BankCurrencyRateClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public List<CurrencyData> getCurrentRates(String bankName) {
        log.info("Fetching current rates for bank: {}", bankName);
        List<CurrencyData> rates = clientFactory.getClient(bankName).getCurrentRates();
        if (rates.isEmpty()) {
            log.warn("No rates found for bank: {}", bankName);
        } else {
            log.info("Fetched {} rates for bank: {}", rates.size(), bankName);
        }
        return sortRates(rates);
    }

    private List<CurrencyData> sortRates(List<CurrencyData> rates) {
        List<String> currencyOrder = List.of("EUR", "USD");
        log.info("Sorting rates for currencies: {}", currencyOrder);
        List<CurrencyData> sortedRates = rates.stream()
                .sorted(Comparator.comparingInt(rate -> currencyOrder.indexOf(rate.getCurrency())))
                .collect(Collectors.toList());
        log.info("Sorted rates: {}", sortedRates);
        return sortedRates;
    }
}
