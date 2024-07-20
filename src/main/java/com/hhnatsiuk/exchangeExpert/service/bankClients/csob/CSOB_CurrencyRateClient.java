package com.hhnatsiuk.exchangeExpert.service.bankClients.csob;

import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import com.hhnatsiuk.exchangeExpert.service.bankClients.BankCurrencyRateClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.hhnatsiuk.exchangeExpert.service.bankClients.csob.CSOB_Parser.parse;

public class CSOB_CurrencyRateClient implements BankCurrencyRateClient {

    private static final Logger log = LogManager.getLogger(CSOB_CurrencyRateClient.class);

    private final WebClient webClient;

    public CSOB_CurrencyRateClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<CurrencyData> getCurrentRates() {
        LocalDate date = LocalDate.now();
        String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE) + "T00:00:00.000%2B02:00";
        String url = "https://www.csob.cz/spa/exrate/exrates/overview/exrates?date=" + dateStr;
        String responseString = "";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            log.info("Fetching current rates from CSOB API for date: {}", dateStr);
            try (CloseableHttpResponse response = client.execute(request)) {
                responseString = EntityUtils.toString(response.getEntity());
                log.debug("Response received: {}", responseString);
            }
        } catch (IOException e) {
            log.error("Error fetching rates from CSOB API: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

        try {
            List<CurrencyData> rates = parse(responseString, "CSOB");
            log.info("Parsed {} currency rates from CSOB API", rates.size());
            return rates;
        } catch (IOException e) {
            log.error("Error parsing rates from CSOB API: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
