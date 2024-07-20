package com.hhnatsiuk.exchangeExpert.controller;

import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import com.hhnatsiuk.exchangeExpert.service.CurrencyRateService.CurrencyRateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CurrencyApiController {

    private static final Logger log = LogManager.getLogger(CurrencyApiController.class);

    private final CurrencyRateService currencyRateService;

    @Autowired
    public CurrencyApiController(CurrencyRateService currencyRateService) {
        this.currencyRateService = currencyRateService;
    }

    @GetMapping("/rates")
    public ResponseEntity<List<CurrencyData>> getCurrentCurrencyRates(@RequestParam String bankName) {
        log.info("Received request for currency rates of bank: {}", bankName);
        List<CurrencyData> rates = currencyRateService.getCurrentRates(bankName);
        if (rates.isEmpty()) {
            log.warn("No currency rates found for bank: {}", bankName);
            return ResponseEntity.noContent().build();
        }
        log.info("Returning {} currency rates for bank: {}", rates.size(), bankName);
        return ResponseEntity.ok(rates);
    }
}
