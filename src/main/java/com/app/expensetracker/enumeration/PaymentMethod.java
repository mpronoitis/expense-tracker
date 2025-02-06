package com.app.expensetracker.enumeration;

import lombok.Getter;

@Getter
public enum PaymentMethod {

    CASH("Cash"),
    CARD("Card"),
    BANK_TRANSFER("Bank Transfer");

    private final String type;

    PaymentMethod(String type) {
        this.type = type;
    }
}
