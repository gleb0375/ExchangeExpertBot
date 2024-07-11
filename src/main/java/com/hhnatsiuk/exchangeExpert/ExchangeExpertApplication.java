package com.hhnatsiuk.exchangeExpert;

import com.hhnatsiuk.exchangeExpert.controller.ExchangeExpertBotController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class ExchangeExpertApplication {


        private static final Logger logger = LogManager.getLogger(ExchangeExpertApplication.class);

        public static void main(String[] args) {

            ApplicationContext ctx = SpringApplication.run(ExchangeExpertApplication.class, args);
            logger.info("Spring Application Context has been started");

            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(ctx.getBean(ExchangeExpertBotController.class));
                logger.info("Telegram bot has been registered successfully");
            } catch (TelegramApiException e) {
                logger.error("Failed to register Telegram bot", e);
            }
        }

}
