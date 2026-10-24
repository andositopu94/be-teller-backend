package com.multipolar.uiteller.backend.service.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionLoggerFactory {
    private final List<TransactionLogger> transactionLoggers;
    private static Logger logger = LoggerFactory.getLogger(TransactionLoggerFactory.class);

    @Autowired
    public TransactionLoggerFactory(List<TransactionLogger> transactionLoggers) {
        this.transactionLoggers = transactionLoggers;
    }

    public TransactionLogger get(String s) {
        TransactionLogger defaultLogger = null;
        for (TransactionLogger logger : transactionLoggers) {
            if (logger.supports(s))
                return logger;

            if (logger.getClass().equals(DefaultTransactionLogger.class))
                defaultLogger = logger;
        }

        if (defaultLogger == null)
            logger.error("No handler found for this type feature, application will not run properly. Feature: " + s);

        // here, no hander support for the requested feature, return default implementation
        return defaultLogger;
    }
}
