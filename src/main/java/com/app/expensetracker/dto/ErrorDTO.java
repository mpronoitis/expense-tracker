package com.app.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDTO {

    private String fieldName;
    private String message;
}
