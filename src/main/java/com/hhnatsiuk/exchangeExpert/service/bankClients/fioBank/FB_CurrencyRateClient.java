package com.hhnatsiuk.exchangeExpert.service.bankClients.fioBank;

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

import static com.hhnatsiuk.exchangeExpert.service.bankClients.fioBank.FB_Parser.parse;

public class FB_CurrencyRateClient implements BankCurrencyRateClient {

    private static final Logger log = LogManager.getLogger(FB_CurrencyRateClient.class);

    private final WebClient webClient;

    public FB_CurrencyRateClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<CurrencyData> getCurrentRates() {
        String url = "https://www.fio.cz/akcie-investice/dalsi-sluzby-fio/devizove-konverze";
        log.info("Sending request to Fio Bank API: {}", url);

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

            //log.info("Response received from Fio Bank API: {}", response);

            List<CurrencyData> rates = parse(response, Bank.FB.getShortName());

            log.info("Parsed {} currency rates from Fio Bank API", rates.size());
            return rates;

        } catch (WebClientResponseException e) {
            log.error("Error fetching rates from Fio Bank API: {}", e.getMessage(), e);
            return List.of();
        } catch (IOException e) {
            log.error("Error parsing rates from Fio Bank API: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
