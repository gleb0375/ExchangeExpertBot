package com.hhnatsiuk.exchangeExpert.service.bankClients.komercniBanka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KB_Parser {

    public static List<CurrencyData> parse(String response, String bankName) throws IOException {
        List<CurrencyData> currencyDataList = new ArrayList<>();
        Document doc = Jsoup.parse(response);

        Element scriptElement = doc.selectFirst("script:containsData(WIDGETS_CONFIG)");

        if (scriptElement != null) {
            String scriptContent = scriptElement.html();

            Pattern pattern = Pattern.compile("\"items\":\\s*(\\[.*?\\])", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(scriptContent);
            if (matcher.find()) {
                String itemsJson = matcher.group(1);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode itemsNode = objectMapper.readTree(itemsJson);

                Iterator<JsonNode> elements = itemsNode.elements();
                while (elements.hasNext()) {
                    JsonNode node = elements.next();
                    String currencyIso = node.get("curencyIso").asText();
                    if ("USD".equals(currencyIso) || "EUR".equals(currencyIso)) {
                        double cashBuy = node.get("cashBuy").asDouble();
                        double cashSell = node.get("cashSell").asDouble();
                        currencyDataList.add(new CurrencyData(currencyIso, cashBuy, cashSell, bankName));
                    }
                }
            } else {
                throw new IOException("JSON data not found in the script content.");
            }
        } else {
            throw new IOException("Script element containing JSON data not found.");
        }
        return currencyDataList;
    }
}
