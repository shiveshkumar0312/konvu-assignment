package com.konvu.log4j;

import com.konvu.log4j.payment.RandomLogging;
import com.konvu.log4j.payment.processor.PaymentProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static Logger LOGGER = LoggerFactory.getLogger(PaymentProcessor.class);
    public static void main(String[] args) {
        int numberOfLogLines = 1000;
        LOGGER.info("Application started...");
        long startTime = System.nanoTime();
        PaymentProcessor processor = new PaymentProcessor("Michael", 110000);
        processor.credit(10000);
        processor.credit(590);
        processor.debit(700);

        RandomLogging.logMillions(numberOfLogLines);


        long endTime = System.nanoTime();

        LOGGER.info("Total time in ns {}", endTime-startTime);
        LOGGER.info("Average time in ns {}", (endTime-startTime)/numberOfLogLines);
    }
}