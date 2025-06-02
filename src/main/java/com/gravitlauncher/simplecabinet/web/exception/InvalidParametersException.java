package com.gravitlauncher.simplecabinet.web.exception;

public class InvalidParametersException extends AbstractCabinetException {

    public InvalidParametersException(String message, int code) {
        super(message, 500 + code);
    }
}
