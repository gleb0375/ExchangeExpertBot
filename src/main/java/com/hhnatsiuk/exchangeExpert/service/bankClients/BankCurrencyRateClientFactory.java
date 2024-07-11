package com.hhnatsiuk.exchangeExpert.service.bankClients;

import com.hhnatsiuk.exchangeExpert.service.bankClients.KomercniBanka.KB_CurrencyRateClient;
import com.hhnatsiuk.exchangeExpert.service.bankClients.RaiffeisenBank.RB_CurrencyRateClient;
import com.hhnatsiuk.exchangeExpert.util.Bank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BankCurrencyRateClientFactory {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public BankCurrencyRateClientFactory(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public BankCurrencyRateClient getClient(String bankName) {
        if (Bank.RB.getFullName().equalsIgnoreCase(bankName)) {
            return new RB_CurrencyRateClient(webClientBuilder.baseUrl("https://api.raiffeisenbank.cz").build());
        }
        if (Bank.KB.getFullName().equalsIgnoreCase(bankName)) {
            return new KB_CurrencyRateClient(webClientBuilder.baseUrl("https://www.kb.cz").build());
        }

        throw new IllegalArgumentException("Unsupported bank: " + bankName);
    }
}
