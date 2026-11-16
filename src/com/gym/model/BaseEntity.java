package com.gym.model;

import java.io.Serializable;

/**
 * Abstract base class demonstrating OOP Inheritance (Unit 2).
 */
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int id;

    public BaseEntity() {
    }

    public BaseEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
