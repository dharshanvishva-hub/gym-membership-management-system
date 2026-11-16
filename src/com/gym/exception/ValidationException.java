package com.gym.exception;

public class ValidationException extends GymException {
    public ValidationException(String message) {
        super("Validation Failed: " + message);
    }
}
