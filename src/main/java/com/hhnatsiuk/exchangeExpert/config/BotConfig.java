package com.hhnatsiuk.exchangeExpert.config;

import com.hhnatsiuk.exchangeExpert.controller.ExchangeExpertBotController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class BotConfig {

    @Bean
    public ExchangeExpertBotController currencyBot() {
        return new ExchangeExpertBotController();
    }

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    public TelegramBotsApi registerBotApi() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            return botsApi;
        } catch (Exception e) {
            throw new RuntimeException("Error initializing Telegram Bot", e);
        }
    }

    public String getBotUsername() {
        return botUsername;
    }

    public String getBotToken() {
        return botToken;
    }
}
