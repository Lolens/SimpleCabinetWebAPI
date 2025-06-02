package com.gravitlauncher.simplecabinet.web.exception;

public class AuthException extends AbstractCabinetException {

    public AuthException(String message) {
        super(message, 1);
    }

    public AuthException(String message, int code) {
        super(message, 1 + code);
    }
}
