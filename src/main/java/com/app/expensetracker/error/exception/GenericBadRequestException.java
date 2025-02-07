package com.app.expensetracker.error.exception;

import com.app.expensetracker.shared.rest.enumeration.ErrorType;

public class GenericBadRequestException extends RuntimeException{
    public ErrorType errorType;

    public GenericBadRequestException(String message) {
        super(message);
    }

    public GenericBadRequestException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }
}
