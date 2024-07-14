package com.hhnatsiuk.exchangeExpert.service.bankClients.raiffeisenBank;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhnatsiuk.exchangeExpert.model.CurrencyData;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class RB_Parser {

    public static List<CurrencyData> parse(String response, String bankName) throws IOException {
        List<CurrencyData> currencyDataList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);
        JsonNode exchangeRates = root.path("exchangeRateLists").get(0).path("exchangeRates");

        for (JsonNode rateNode : exchangeRates) {
            String currencyCode = rateNode.path("currencyCode").asText();
            if ("EUR".equals(currencyCode) || "USD".equals(currencyCode)) {
                double buyRate = 1 / rateNode.path("buyingRate").asDouble();
                double sellRate = 1 / rateNode.path("saleRate").asDouble();

                buyRate = BigDecimal.valueOf(buyRate).setScale(4, RoundingMode.HALF_UP).doubleValue();
                sellRate = BigDecimal.valueOf(sellRate).setScale(4, RoundingMode.HALF_UP).doubleValue();

                CurrencyData data = new CurrencyData(currencyCode, buyRate, sellRate, bankName);
                currencyDataList.add(data);
            }
        }
        return currencyDataList;
    }

}
