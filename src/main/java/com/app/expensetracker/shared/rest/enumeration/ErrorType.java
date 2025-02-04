package com.app.expensetracker.shared.rest.enumeration;

import lombok.Getter;

@Getter
public enum ErrorType {
    IM_BAD_USERNAME("001","Username is not valid"),
    IM_ARGUMENT("002", "Arguments are not valid");

    private final String code;
    private final String message;

     ErrorType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
