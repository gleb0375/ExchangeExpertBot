package com.hhnatsiuk.exchangeExpert.service.bankClients.raiffeisenBank;

import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import com.hhnatsiuk.exchangeExpert.service.bankClients.BankCurrencyRateClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.List;

import static com.hhnatsiuk.exchangeExpert.service.bankClients.raiffeisenBank.RB_Parser.parse;

public class RB_CurrencyRateClient implements BankCurrencyRateClient {


    private final WebClient webClient;

    public RB_CurrencyRateClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<CurrencyData> getCurrentRates() {
        try {
            String response = webClient.get()
                    .uri("https://www.rb.cz/frontend-controller/backend-data/currency/dcc/listActual")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            List<CurrencyData> rates = parse(response, "rb");

            return rates;

        } catch (WebClientResponseException e) {
            e.printStackTrace();
            return List.of();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
