package com.gym.exception;

/**
 * Base custom exception for the Gym Management System.
 * Demonstrates Unit 3: Exception Handling in 25ITT32.
 */
public class GymException extends Exception {
    public GymException(String message) {
        super(message);
    }

    public GymException(String message, Throwable cause) {
        super(message, cause);
    }
}
