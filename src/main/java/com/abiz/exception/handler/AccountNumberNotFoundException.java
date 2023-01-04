package com.abiz.exception.handler;


public class AccountNumberNotFoundException extends RuntimeException {
    public AccountNumberNotFoundException() {
        super("account number not found!");
    }
}
