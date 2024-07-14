package com.hhnatsiuk.exchangeExpert.service.bankClients.fioBank;

import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import com.hhnatsiuk.exchangeExpert.service.bankClients.BankCurrencyRateClient;
import com.hhnatsiuk.exchangeExpert.util.Bank;
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


public class FB_CurrencyRateClient  implements BankCurrencyRateClient {

    private final WebClient webClient;

    public FB_CurrencyRateClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<CurrencyData> getCurrentRates() {
        try {
            Flux<DataBuffer> responseFlux = webClient.get()
                    .uri("https://www.fio.cz/akcie-investice/dalsi-sluzby-fio/devizove-konverze")
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

            List<CurrencyData> rates = parse(response, Bank.KB.getShortName());

            return rates;

        } catch (WebClientResponseException e) {
            e.printStackTrace();
            return List.of();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
