package com.hhnatsiuk.exchangeExpert.service.bankClients.csob;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import com.hhnatsiuk.exchangeExpert.service.bankClients.Parser;
import com.hhnatsiuk.exchangeExpert.util.Currency;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSOB_Parser implements Parser {

    private static final Logger log = LogManager.getLogger(CSOB_Parser.class);

    public static List<CurrencyData> parse(String response, String bankName) throws IOException {
        log.info("Parsing response from CSOB API");
        List<CurrencyData> currencyDataList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response);
        JsonNode exchangeRates = rootNode.path("exchangeRates");

        for (JsonNode node : exchangeRates) {
            String currency = node.path("currency").asText();
            if (Currency.USD.toString().equals(currency) || Currency.EUR.toString().equals(currency)) {
                double buyRate = node.path("cash").path("purchase").asDouble();
                double sellRate = node.path("cash").path("sale").asDouble();

                CurrencyData currencyData = new CurrencyData(currency, buyRate, sellRate, bankName);
                currencyDataList.add(currencyData);
                log.debug("Parsed rate for {}: Buy - {}, Sell - {}", currency, buyRate, sellRate);
            }
        }
        log.info("Finished parsing response, found {} rates", currencyDataList.size());
        return currencyDataList;
    }
}
