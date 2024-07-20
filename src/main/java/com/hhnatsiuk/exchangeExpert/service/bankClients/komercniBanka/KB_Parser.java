package com.hhnatsiuk.exchangeExpert.service.bankClients.komercniBanka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import com.hhnatsiuk.exchangeExpert.service.bankClients.Parser;
import com.hhnatsiuk.exchangeExpert.util.Currency;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KB_Parser implements Parser {

    private static final Logger log = LogManager.getLogger(KB_Parser.class);

    public static List<CurrencyData> parse(String response, String bankName) throws IOException {
        log.info("Parsing response from Komercni Banka API");
        List<CurrencyData> currencyDataList = new ArrayList<>();
        Document doc = Jsoup.parse(response);

        Element scriptElement = doc.selectFirst("script:containsData(WIDGETS_CONFIG)");

        if (scriptElement != null) {
            String scriptContent = scriptElement.html();
            log.debug("Found script element containing JSON data.");

            Pattern pattern = Pattern.compile("\"items\":\\s*(\\[.*?\\])", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(scriptContent);
            if (matcher.find()) {
                String itemsJson = matcher.group(1);
                log.debug("Extracted JSON data: {}", itemsJson);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode itemsNode = objectMapper.readTree(itemsJson);

                Iterator<JsonNode> elements = itemsNode.elements();
                while (elements.hasNext()) {
                    JsonNode node = elements.next();
                    String currencyIso = node.get("curencyIso").asText();
                    if (Currency.USD.toString().equals(currencyIso) || Currency.EUR.toString().equals(currencyIso)) {
                        double cashBuy = node.get("cashBuy").asDouble();
                        double cashSell = node.get("cashSell").asDouble();
                        currencyDataList.add(new CurrencyData(currencyIso, cashBuy, cashSell, bankName));

                        log.debug("Parsed rate for {}: Buy - {}, Sell - {}", currencyIso, cashBuy, cashSell);
                    }
                }
            } else {
                log.error("JSON data not found in the script content.");
                throw new IOException("JSON data not found in the script content.");
            }
        } else {
            log.error("Script element containing JSON data not found.");
            throw new IOException("Script element containing JSON data not found.");
        }
        log.info("Finished parsing response, found {} rates", currencyDataList.size());
        return currencyDataList;
    }
}
