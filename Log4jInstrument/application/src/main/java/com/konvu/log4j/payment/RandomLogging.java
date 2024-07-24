package com.konvu.log4j.payment;

import com.konvu.log4j.payment.processor.PaymentProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class RandomLogging {
    private static Logger LOGGER = LoggerFactory.getLogger(PaymentProcessor.class);
    public static void logMillions(int number){
        LOGGER.info("Starting random log...");
        String s = null;
        IntStream.range(0, number).forEach(n -> {
            LOGGER.error("Can't authorize amount {}", 234);
            LOGGER.info("Line 2 {}", s);
            LOGGER.debug("Line 3 {}", "dgshdgshdsgh");
            LOGGER.trace("Line 4 {} {} {}", "{{}}{??i////}}", 1234, "abs");
        });

    }
}
