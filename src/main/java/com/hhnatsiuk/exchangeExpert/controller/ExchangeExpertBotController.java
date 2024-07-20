package com.hhnatsiuk.exchangeExpert.controller;

import com.hhnatsiuk.exchangeExpert.config.BotConfig;
import com.hhnatsiuk.exchangeExpert.model.CurrencyData;
import com.hhnatsiuk.exchangeExpert.service.CurrencyRateService.CurrencyRateService;
import com.hhnatsiuk.exchangeExpert.util.BotCommand;
import com.hhnatsiuk.exchangeExpert.util.Bank;
import com.hhnatsiuk.exchangeExpert.util.Currency;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class ExchangeExpertBotController extends TelegramLongPollingBot {

    private static final Logger log = LogManager.getLogger(ExchangeExpertBotController.class);

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
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            log.info("Received message: {} from chatId: {}", messageText, chatId);
            handleTextMessage(messageText, chatId);
        }
    }

    private void handleTextMessage(String messageText, Long chatId) {
        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());

        String[] parts = messageText.split(" ", 5);
        String commandText = parts[0];

        BotCommand command = BotCommand.fromCommandText(commandText);

        if (command == null) {
            response.setText("Command not recognized. Please use /start to begin.");
            log.warn("Unrecognized command: {}", commandText);
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
                        handleSingleRateCommand(response, parts[1]);
                    } else {
                        response.setText("Please specify a bank code, e.g., /rate rb, /rate kb ...");
                    }
                    break;
                case CALCULATE:
                    if (parts.length == 5) {
                        try {
                            double amount = Double.parseDouble(parts[4]);
                            handleCalculateCommand(response, parts[1], parts[2], parts[3], amount);
                        } catch (NumberFormatException e) {
                            response.setText("Invalid amount. Please enter a valid number.");
                            log.error("Invalid amount provided: {}", parts[4], e);
                        }
                    } else {
                        response.setText("Please provide all necessary parameters, e.g., /calculate <bank> <from_currency> <to_currency> <amount>.");
                    }
                    break;
                case BANKS:
                    handleBanksCommand(response);
                    break;
                case HELP:
                    handleHelpCommand(response);
                    break;
                default:
                    response.setText("This command is not supported yet.");
                    log.warn("Unsupported command: {}", commandText);
                    break;
            }
        }

        try {
            execute(response);
            log.info("Response sent to chatId: {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Error sending response to chatId: {}", chatId, e);
        }
    }

    private void handleAllRatesCommand(SendMessage response) {
        StringBuilder ratesMessage = new StringBuilder("Current rates for all banks:\n\n");

        for (Bank bank : Bank.values()) {
            log.info("Fetching rates for bank: {}", bank.getFullName());
            List<CurrencyData> rates = currencyRateService.getCurrentRates(bank.getFullName());
            ratesMessage.append("Rates for ").append(bank.getFullName()).append(":\n");
            for (CurrencyData rate : rates) {
                ratesMessage.append(rate.getCurrency())
                        .append(": we Buy - ").append(rate.getBuyRate())
                        .append(", we Sell - ").append(rate.getSellRate())
                        .append("\n");
            }
            ratesMessage.append("\n");
        }

        response.setText(ratesMessage.toString());
        log.info("Rates for all banks fetched successfully.");
    }

    private void handleSingleRateCommand(SendMessage response, String bankCode) {
        String bankName = getBankNameByCode(bankCode, response);
        if (bankName == null) {
            log.warn("Bank code not recognized: {}", bankCode);
            return;
        }

        log.info("Fetching rates for bank: {}", bankName);
        List<CurrencyData> rates = currencyRateService.getCurrentRates(bankName);
        StringBuilder ratesMessage = new StringBuilder("Current rates for " + bankName + ":\n");
        for (CurrencyData rate : rates) {
            ratesMessage.append(rate.getCurrency())
                    .append(": we Buy - ").append(rate.getBuyRate())
                    .append(", we Sell - ").append(rate.getSellRate())
                    .append("\n");
        }
        response.setText(ratesMessage.toString());
        log.info("Rates for bank {} fetched successfully.", bankName);
    }

    private void handleHelpCommand(SendMessage response) {
        response.setText(BotCommand.getAllCommands());
        log.info("Help command executed.");
    }

    private void handleBanksCommand(SendMessage response) {
        response.setText(Bank.getAllBanksInfo());
        log.info("Banks command executed.");
    }

    private void handleCalculateCommand(SendMessage response, String bankCode, String from, String to, double amount) {
        String bankName = getBankNameByCode(bankCode, response);
        if (bankName == null) {
            log.warn("Bank code not recognized: {}", bankCode);
            return;
        }

        log.info("Calculating conversion for bank: {} from: {} to: {} amount: {}", bankName, from, to, amount);
        List<CurrencyData> rates = currencyRateService.getCurrentRates(bankName);
        double fromRateInCZK = 1.0, toRateInCZK = 1.0;

        if (!from.equals(Currency.CZK.toString())) {
            for (CurrencyData rate : rates) {
                if (rate.getCurrency().equalsIgnoreCase(from)) {
                    fromRateInCZK = rate.getSellRate();
                    break;
                }
            }
        }

        if (!to.equals(Currency.CZK.toString())) {
            for (CurrencyData rate : rates) {
                if (rate.getCurrency().equalsIgnoreCase(to)) {
                    toRateInCZK = rate.getBuyRate();
                    break;
                }
            }
        }

        if (fromRateInCZK == 0 || toRateInCZK == 0) {
            response.setText("One of the currency rates not found. Please check the available currencies.");
            log.warn("Currency rate not found for from: {} or to: {}", from, to);
            return;
        }

        double convertedAmount;
        if (from.equals(Currency.CZK.toString())) {
            convertedAmount = amount / toRateInCZK;
        } else if (to.equals(Currency.CZK.toString())) {
            convertedAmount = amount * fromRateInCZK;
        } else {
            convertedAmount = (amount * fromRateInCZK) / toRateInCZK;
        }

        String result = String.format("Converted amount: %.2f %s equals to %.2f %s in %s.", amount, from, convertedAmount, to, bankName);
        response.setText(result);
        log.info("Conversion result: {}", result);
    }

    private String getBankNameByCode(String bankCode, SendMessage response) {
        Bank bank = Bank.getByShortName(bankCode);
        if (bank == null) {
            response.setText("Bank code not recognized.");
            return null;
        }
        return bank.getFullName();
    }
}
