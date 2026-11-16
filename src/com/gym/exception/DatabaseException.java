package com.gym.exception;

public class DatabaseException extends GymException {
    public DatabaseException(String message, Throwable cause) {
        super("Database Error: " + message, cause);
    }

    public DatabaseException(String message) {
        super("Database Error: " + message);
    }
}
