package com.hhnatsiuk.exchangeExpert.util;

public enum BotCommand {
    START("/start", "Start the bot."),
    RATE("/rate", "Get a bank's rate. Usage: /rate <bank_code>."),
    RATES_ALL("/rates", "Get all banks' rates."),
    CALCULATE("/calculate", "Currency calculator. Usage: /calculate <bank_code> <from_currency> <to_currency> <amount>. Example: /calculate rb eur usd 1000"),
    HELP("/help", "List of commands."),
    BANKS("/banks", "List of banks");

    private final String command;
    private final String description;

    BotCommand(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public static BotCommand fromCommandText(String text) {
        for (BotCommand command : values()) {
            if (command.command.equals(text)) {
                return command;
            }
        }
        return null;
    }

    public static String getAllCommands() {
        StringBuilder commands = new StringBuilder();
        for (BotCommand command : values()) {
            commands.append(command.command).append(" - ").append(command.description).append("\n");
        }
        return commands.toString();
    }

    @Override
    public String toString() {
        return this.command;
    }
}
