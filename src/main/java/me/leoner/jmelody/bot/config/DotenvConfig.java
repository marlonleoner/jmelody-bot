package me.leoner.jmelody.bot.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DotenvConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(DotenvConfig.class);

    public static void run() {
        Dotenv dotenv = Dotenv.configure()
                .filename(".env")
                .load();

        dotenv.entries().forEach(e -> {
            LOGGER.info("Setting property {}={}", e.getKey(), e.getValue());
            System.setProperty(e.getKey(), e.getValue());
        });
    }
}
