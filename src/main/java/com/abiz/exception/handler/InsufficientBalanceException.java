package com.abiz.exception.handler;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("insufficient balance!");
    }
}

