package com.gravitlauncher.simplecabinet.web.exception;

public class BalanceException extends AbstractCabinetException {

    public BalanceException(String message) {
        super(message, 100);
    }

    public BalanceException(String message, int code) {
        super(message, 100 + code);
    }
}
