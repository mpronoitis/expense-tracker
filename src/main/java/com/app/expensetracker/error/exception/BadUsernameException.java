package com.app.expensetracker.error.exception;

public class BadUsernameException extends RuntimeException{

    public BadUsernameException(String message) {
        super(message);
    }
}
