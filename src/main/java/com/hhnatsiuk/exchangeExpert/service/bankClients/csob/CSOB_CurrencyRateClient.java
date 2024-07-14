package com.hhnatsiuk.exchangeExpert.service.bankClients.csob;

import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import com.hhnatsiuk.exchangeExpert.service.bankClients.BankCurrencyRateClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.hhnatsiuk.exchangeExpert.service.bankClients.csob.CSOB_Parser.parse;

public class CSOB_CurrencyRateClient implements BankCurrencyRateClient {

    private final WebClient webClient;

    public CSOB_CurrencyRateClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<CurrencyData> getCurrentRates() {
        LocalDate date = LocalDate.now();
        String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE) + "T00:00:00.000%2B02:00";
        String url = "https://www.csob.cz/spa/exrate/exrates/overview/exrates?date=" + dateStr;
        String responseS = "";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            System.out.println("Request URL: " + request.getURI());
            try (CloseableHttpResponse response = client.execute(request)) {
                responseS = EntityUtils.toString(response.getEntity());
                System.out.println("Response: " + responseS);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            return parse(responseS, "CSOB");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
