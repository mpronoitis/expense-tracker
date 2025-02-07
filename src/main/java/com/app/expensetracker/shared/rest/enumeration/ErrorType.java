package com.app.expensetracker.shared.rest.enumeration;

import lombok.Getter;

@Getter
public enum ErrorType {
    IM_BAD_USERNAME("001","Username is not valid"),
    IM_ARGUMENT("002", "Arguments are not valid"),
    IM_USER_NOT_FOUND("003", "User not found"),
    IM_BAD_CREDENTIALS("004", "Bad Credentials for this user"),
    IM_UNAUTHORIZED("005", "Unauthorized: Access Denied"),
    IM_GENERIC("006", "Generic Error"),
    IM_CATEGORY_NOT_FOUND("007", "Category not found"),
    IM_BUDGET_NOT_FOUND("008", "Budget not found"),
    IM_BUDGET_EXCEEDED("009", "Budget Limit is exceeded");

    private final String code;
    private final String message;

     ErrorType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
