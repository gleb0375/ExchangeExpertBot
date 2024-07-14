package com.hhnatsiuk.exchangeExpert.controller;

import com.hhnatsiuk.exchangeExpert.config.BotConfig;
import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import com.hhnatsiuk.exchangeExpert.service.CurrencyRateService.CurrencyRateService;
import com.hhnatsiuk.exchangeExpert.util.BotCommand;
import com.hhnatsiuk.exchangeExpert.util.Bank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class ExchangeExpertBotController extends TelegramLongPollingBot {

    private static final Logger logger = LogManager.getLogger(ExchangeExpertBotController.class);

    @Autowired
    private BotConfig config;

    @Autowired
    private CurrencyRateService currencyRateService;

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleTextMessage(update.getMessage().getText(), update.getMessage().getChatId());
        }
    }

    private void handleTextMessage(String messageText, Long chatId) {
        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());

        String[] parts = messageText.split(" ", 2);
        String commandText = parts[0];

        BotCommand command = BotCommand.fromCommandText(commandText);

        if (command == null) {
            response.setText("Command not recognized. Please use /start to begin.");
        } else {
            switch (command) {
                case START:
                    response.setText("Hello! \uD83D\uDC4B I’m your go-to bot for currency exchange rates and calculations in Czech banks. Type /help for commands.");
                    break;
                case RATES_ALL:
                    handleAllRatesCommand(response);
                    break;
                case RATE:
                    if (parts.length > 1) {
                        handleSingleRateCommand(parts[1], response);
                    } else {
                        response.setText("Please specify a bank code, e.g., /rate rb or /rate kb.");
                    }
                    break;
                case CALCULATE:
                    response.setText("Currency calculator feature is under development.");
                    break;
                case BANKS:
                    handleBanksCommand(response);
                    break;
                case HELP:
                    handleHelpCommand(response);
                    break;
                default:
                    response.setText("This command is not supported yet.");
                    break;
            }
        }

        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleAllRatesCommand(SendMessage response) {
        StringBuilder ratesMessage = new StringBuilder("Current rates for all banks:\n\n");

        String[] banks = {"Raiffeisenbank", "Komercni banka", "Fio banka", "CSOB"};
        for (String bank : banks) {
            List<CurrencyData> rates = currencyRateService.getCurrentRates(bank);
            ratesMessage.append("Rates for ").append(bank).append(":\n");
            for (CurrencyData rate : rates) {
                ratesMessage.append(rate.getCurrency())
                        .append(": Buy - ").append(rate.getBuyRate())
                        .append(", Sell - ").append(rate.getSellRate())
                        .append("\n");
            }
            ratesMessage.append("\n");
        }

        response.setText(ratesMessage.toString());
    }

    private void handleSingleRateCommand(String bankCode, SendMessage response) {
        bankCode = bankCode.trim().toLowerCase();
        String bankName = "";

        switch (bankCode) {
            case "rb":
                bankName = Bank.RB.getFullName();
                break;
            case "kb":
                bankName = Bank.KB.getFullName();
                break;
            case "fb":
                bankName = Bank.FB.getFullName();
                break;
            case "csob":
                bankName = Bank.CSOB.getFullName();
                break;
            default:
                response.setText("Bank code not recognized yet. Please use /rate <rb|kb>.");
                return;
        }

        List<CurrencyData> rates = currencyRateService.getCurrentRates(bankName);
        StringBuilder ratesMessage = new StringBuilder("Current rates for " + bankName + ":\n");
        for (CurrencyData rate : rates) {
            ratesMessage.append(rate.getCurrency())
                    .append(": Buy - ").append(rate.getBuyRate())
                    .append(", Sell - ").append(rate.getSellRate())
                    .append("\n");
        }
        response.setText(ratesMessage.toString());
    }

    private void handleHelpCommand(SendMessage response) {
        response.setText(BotCommand.getAllCommands());
    }

    private void handleBanksCommand(SendMessage response) {
        response.setText(Bank.getAllBanksInfo());
    }
}
