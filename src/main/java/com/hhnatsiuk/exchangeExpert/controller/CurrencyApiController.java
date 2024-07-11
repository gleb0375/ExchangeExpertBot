package com.hhnatsiuk.exchangeExpert.controller;

import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import com.hhnatsiuk.exchangeExpert.service.CurrencyRateService.CurrencyRateService;
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

    private final CurrencyRateService currencyRateService;

    @Autowired
    public CurrencyApiController(CurrencyRateService currencyRateService) {
        this.currencyRateService = currencyRateService;
    }

    @GetMapping("/rates")
    public ResponseEntity<List<CurrencyData>> getCurrentCurrencyRates(@RequestParam String bankName) {
        List<CurrencyData> rates = currencyRateService.getCurrentRates(bankName);
        return ResponseEntity.ok(rates);
    }
}