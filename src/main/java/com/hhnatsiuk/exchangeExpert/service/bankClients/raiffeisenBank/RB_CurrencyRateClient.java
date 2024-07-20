package com.hhnatsiuk.exchangeExpert.service.bankClients.raiffeisenBank;

import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import com.hhnatsiuk.exchangeExpert.service.bankClients.BankCurrencyRateClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.List;

import static com.hhnatsiuk.exchangeExpert.service.bankClients.raiffeisenBank.RB_Parser.parse;

public class RB_CurrencyRateClient implements BankCurrencyRateClient {

    private static final Logger log = LogManager.getLogger(RB_CurrencyRateClient.class);

    private final WebClient webClient;

    public RB_CurrencyRateClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<CurrencyData> getCurrentRates() {
        try {
            log.info("Fetching current rates from Raiffeisenbank API");
            String response = webClient.get()
                    .uri("https://www.rb.cz/frontend-controller/backend-data/currency/dcc/listActual")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            //log.debug("Response received: {}", response);

            List<CurrencyData> rates = parse(response, "rb");
            log.info("Parsed {} currency rates", rates.size());

            return rates;

        } catch (WebClientResponseException e) {
            log.error("Error fetching rates from Raiffeisenbank API: {}", e.getMessage(), e);
            return List.of();
        } catch (IOException e) {
            log.error("Error parsing rates from Raiffeisenbank API: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
