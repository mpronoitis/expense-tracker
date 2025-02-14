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
    IM_BUDGET_EXCEEDED("009", "Budget Limit is exceeded"),
    IM_SECURITY_CONTROL_ERROR("010","Security Control Error: You try to get a resource that does not belongs to you"),
    IM_INVALID_EXPENSE_DATE("011","Expense date cannot be in the future"),
    IM_INVALID_DATE("012", "Start date cannot be in the past"),
    IM_INVALID_DATE_RANGE("013", "Start date cannot be after the end date"),
    IM_INVALID_UPDATE_EXPENSE_DATE("014", "Provided Expense data is already on existing time budget frame"),
    IM_INVALID_EXPENSE_AMOUNT("015", "Expense amount must be greater than zero");

    private final String code;
    private final String message;

     ErrorType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
