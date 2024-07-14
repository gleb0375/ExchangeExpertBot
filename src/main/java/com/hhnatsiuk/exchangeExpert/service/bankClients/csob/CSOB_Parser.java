package com.hhnatsiuk.exchangeExpert.service.bankClients.csob;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhnatsiuk.exchangeExpert.model.CurrencyData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSOB_Parser {

    public static List<CurrencyData> parse(String response, String bankName) throws IOException {
        List<CurrencyData> currencyDataList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response);
        JsonNode exchangeRates = rootNode.path("exchangeRates");

        for (JsonNode node : exchangeRates) {
            String currency = node.path("currency").asText();
            if ("USD".equals(currency) || "EUR".equals(currency)) {
                double buyRate = node.path("cash").path("purchase").asDouble();
                double sellRate = node.path("cash").path("sale").asDouble();

                CurrencyData currencyData = new CurrencyData(currency, buyRate, sellRate, bankName);
                currencyDataList.add(currencyData);
            }
        }

        return currencyDataList;
    }
}
