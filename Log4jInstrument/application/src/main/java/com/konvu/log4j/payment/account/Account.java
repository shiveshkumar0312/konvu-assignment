package com.konvu.log4j.payment.account;

import com.konvu.log4j.payment.error.InsufficientBalanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Account {
    private static Logger LOGGER = LoggerFactory.getLogger(Account.class);
    private String userName;
    private double balance;
    private double authorizedBalance = 0;
    private boolean authorized = false;

    public Account(String userName, double balance) {
        this.userName = userName;
        this.balance = balance;
    }


    public double accountBalance(){
        var availableBalance = balance - authorizedBalance;
        LOGGER.debug("Account balance is {}", availableBalance);
        return availableBalance;
    }
    public void authorize(double amount) throws InsufficientBalanceException {
        if(amount > accountBalance()) {
            LOGGER.error("Can't authorize amount {}", amount);
            throw new InsufficientBalanceException("Current Balance is lower than requested amount");

        }
        authorized = true;
        authorizedBalance += amount;
        LOGGER.info("Authorization of amount {} is done", amount);
    }

    public void unAuthorize(double amount){
        authorized = false;
        authorizedBalance -= amount;
        LOGGER.info("Authorization removed of amount {}", amount);
    }

    public void debit(double amount){
        if (authorized && balance > amount){
            balance -= amount;
            LOGGER.info("Debit of amount {} is done from user {}'s account. New balance is {}", amount, userName, accountBalance());
        } else
            LOGGER.error("Debit can't be performed of amount {} due to low balance {} from user {}", amount, accountBalance(), userName);
    }

    public void credit(double amount){
         balance += amount;
         LOGGER.info("Current state: {}", this.toString());
         LOGGER.info("Credit of amount {} is done for user {}. New balance is {}", amount, userName, accountBalance());
    }

    @Override
    public String toString() {
        return "Account{" +
                "userName='" + userName + '\'' +
                ", balance=" + balance +
                ", authorizedBalance=" + authorizedBalance +
                ", authorized=" + authorized +
                '}';
    }

}
