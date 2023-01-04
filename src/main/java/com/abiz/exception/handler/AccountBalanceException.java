package com.abiz.exception.handler;


public class AccountBalanceException extends RuntimeException {
    public AccountBalanceException(double unbalancy) {
        super("account is unbalance ! :" + unbalancy);
    }
}
