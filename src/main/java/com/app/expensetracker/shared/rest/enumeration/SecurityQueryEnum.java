package com.app.expensetracker.shared.rest.enumeration;


public enum SecurityQueryEnum {
    RETRIEVE_USER("""
            select us.id from User us
            where us.id = :id
            """);

    private final String text;

    SecurityQueryEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
