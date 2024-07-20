package com.hhnatsiuk.exchangeExpert.service.bankClients.komercniBanka;

import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import com.hhnatsiuk.exchangeExpert.service.bankClients.BankCurrencyRateClient;
import com.hhnatsiuk.exchangeExpert.util.Bank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static com.hhnatsiuk.exchangeExpert.service.bankClients.komercniBanka.KB_Parser.parse;

public class KB_CurrencyRateClient implements BankCurrencyRateClient {

    private static final Logger log = LogManager.getLogger(KB_CurrencyRateClient.class);

    private final WebClient webClient;

    public KB_CurrencyRateClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<CurrencyData> getCurrentRates() {
        String url = "https://www.kb.cz/cs/kurzovni-listek";
        log.info("Sending request to Komercni Banka API: {}", url);

        try {
            Flux<DataBuffer> responseFlux = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToFlux(DataBuffer.class);

            String response = responseFlux
                    .map(buffer -> {
                        byte[] bytes = new byte[buffer.readableByteCount()];
                        buffer.read(bytes);
                        DataBufferUtils.release(buffer);
                        return new String(bytes, StandardCharsets.UTF_8);
                    })
                    .collect(Collectors.joining())
                    .block();

            //log.info("Response received from Komercni Banka API: {}", response);

            List<CurrencyData> rates = parse(response, Bank.KB.getShortName());

            log.info("Parsed {} currency rates from Komercni Banka API", rates.size());
            return rates;

        } catch (WebClientResponseException e) {
            log.error("Error fetching rates from Komercni Banka API: {}", e.getMessage(), e);
            return List.of();
        } catch (IOException e) {
            log.error("Error parsing rates from Komercni Banka API: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
