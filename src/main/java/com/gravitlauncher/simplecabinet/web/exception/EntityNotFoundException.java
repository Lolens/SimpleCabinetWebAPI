package com.gravitlauncher.simplecabinet.web.exception;

public class EntityNotFoundException extends AbstractCabinetException {

    public EntityNotFoundException(String message) {
        super(message, 499);
    }
}
