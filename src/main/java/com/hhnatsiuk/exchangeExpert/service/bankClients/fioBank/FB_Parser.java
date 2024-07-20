package com.hhnatsiuk.exchangeExpert.service.bankClients.fioBank;

import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import com.hhnatsiuk.exchangeExpert.service.bankClients.Parser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FB_Parser implements Parser {

    private static final Logger log = LogManager.getLogger(FB_Parser.class);

    public static List<CurrencyData> parse(String response, String bankName) throws IOException {
        log.info("Parsing response from Fio Bank API");
        List<CurrencyData> currencyDataList = new ArrayList<>();

        Document doc = Jsoup.parse(response);
        Elements rows = doc.select("table.tbl-sazby tbody tr");

        for (Element row : rows) {
            Elements columns = row.select("td");

            String currency = columns.get(0).text();
            if (USD.equals(currency) || EUR.equals(currency)) {
                double buyRate = Double.parseDouble(columns.get(3).text().replace(",", "."));
                double sellRate = Double.parseDouble(columns.get(4).text().replace(",", "."));

                CurrencyData currencyData = new CurrencyData(currency, buyRate, sellRate, bankName);
                currencyDataList.add(currencyData);

                log.debug("Parsed rate for {}: Buy - {}, Sell - {}", currency, buyRate, sellRate);
            }
        }
        log.info("Finished parsing response, found {} rates", currencyDataList.size());
        return currencyDataList;
    }
}
