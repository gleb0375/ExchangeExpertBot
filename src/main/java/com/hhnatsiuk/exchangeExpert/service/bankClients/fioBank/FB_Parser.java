package com.hhnatsiuk.exchangeExpert.service.bankClients.fioBank;

import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FB_Parser {

    public static List<CurrencyData> parse(String response, String bankName) throws IOException {
        List<CurrencyData> currencyDataList = new ArrayList<>();

        Document doc = Jsoup.parse(response);
        Elements rows = doc.select("table.tbl-sazby tbody tr");

        for (Element row : rows) {
            Elements columns = row.select("td");

            String currency = columns.get(0).text();
            if (currency.equals("USD") || currency.equals("EUR")) {
                double buyRate = Double.parseDouble(columns.get(3).text().replace(",", "."));
                double sellRate = Double.parseDouble(columns.get(4).text().replace(",", "."));

                CurrencyData currencyData = new CurrencyData(currency, buyRate, sellRate, bankName);
                currencyDataList.add(currencyData);
            }
        }

        return currencyDataList;
    }
}
