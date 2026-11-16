package com.gym.exception;

public class EntityNotFoundException extends GymException {
    public EntityNotFoundException(String entityName, Object id) {
        super(entityName + " with ID '" + id + "' was not found.");
    }
}
