package com.konvu.log4j.payment.processor;

import com.konvu.log4j.payment.error.InsufficientBalanceException;
import com.konvu.log4j.payment.account.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentProcessor {
    private static Logger LOGGER = LoggerFactory.getLogger(PaymentProcessor.class);
    Account account;

    public PaymentProcessor(String userName, double startingBalance) {
        this.account = new Account(userName, startingBalance);
    }

    public void debit(double amount){
        try {
            account.authorize(amount);
        } catch (InsufficientBalanceException e) {
            //LOGGER.error("Insufficient fund", e);
            return;
        }
        account.debit(amount);
        account.unAuthorize(amount);
    }

    public void credit(double amount){
        account.credit(amount);
    }
}
