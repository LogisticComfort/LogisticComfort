package com.logisticcomfort;

import com.logisticcomfort.telegram.bot.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class LogisticComfortApplication {

    static  final Logger LOG = LoggerFactory.getLogger(LogisticComfortApplication.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(LogisticComfortApplication.class, args);

        LOG.info("Before Starting application");
        LOG.debug("Starting my application in debug with {} args", args.length);
        LOG.info("Starting my application with {} args.", args.length);
    }

}
