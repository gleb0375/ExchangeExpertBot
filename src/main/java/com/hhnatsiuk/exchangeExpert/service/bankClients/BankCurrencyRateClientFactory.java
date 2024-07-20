package com.hhnatsiuk.exchangeExpert.service.bankClients;

import com.hhnatsiuk.exchangeExpert.service.bankClients.csob.CSOB_CurrencyRateClient;
import com.hhnatsiuk.exchangeExpert.service.bankClients.fioBank.FB_CurrencyRateClient;
import com.hhnatsiuk.exchangeExpert.service.bankClients.komercniBanka.KB_CurrencyRateClient;
import com.hhnatsiuk.exchangeExpert.service.bankClients.raiffeisenBank.RB_CurrencyRateClient;
import com.hhnatsiuk.exchangeExpert.util.Bank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BankCurrencyRateClientFactory {

    private static final Logger log = LogManager.getLogger(BankCurrencyRateClientFactory.class);

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public BankCurrencyRateClientFactory(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public BankCurrencyRateClient getClient(String bankName) {
        log.info("Creating currency rate client for bank: {}", bankName);

        if (Bank.RB.getFullName().equalsIgnoreCase(bankName)) {
            log.debug("Creating Raiffeisenbank client");
            return new RB_CurrencyRateClient(webClientBuilder.baseUrl("https://api.raiffeisenbank.cz").build());
        }
        if (Bank.KB.getFullName().equalsIgnoreCase(bankName)) {
            log.debug("Creating Komercni banka client");
            return new KB_CurrencyRateClient(webClientBuilder.baseUrl("https://www.kb.cz").build());
        }
        if (Bank.FB.getFullName().equalsIgnoreCase(bankName)) {
            log.debug("Creating Fio banka client");
            return new FB_CurrencyRateClient(webClientBuilder.baseUrl("https://www.fio.cz").build());
        }
        if (Bank.CSOB.getFullName().equalsIgnoreCase(bankName)) {
            log.debug("Creating CSOB client");
            return new CSOB_CurrencyRateClient(webClientBuilder.baseUrl("https://www.csob.cz").build());
        }

        log.error("Unsupported bank: {}", bankName);
        throw new IllegalArgumentException("Unsupported bank: " + bankName);
    }
}
