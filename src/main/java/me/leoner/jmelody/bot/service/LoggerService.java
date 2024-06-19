package me.leoner.jmelody.bot.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoggerService {

    public static void debug(Class<?> context, String format, Object... args) {
        Logger logger = getLogger(context);
        logger.debug(format, args);
    }

    public static void info(Class<?> context, String format, Object... args) {
        Logger logger = getLogger(context);
        logger.info(format, args);
    }

    public static void warn(Class<?> context, String format, Object... args) {
        Logger logger = getLogger(context);
        logger.warn(format, args);
    }

    public static void error(Class<?> context, String format, Object... args) {
        Logger logger = getLogger(context);
        logger.error(format, args);
    }

    private static Logger getLogger(Class<?> context) {
        return LoggerFactory.getLogger(context);
    }
}
