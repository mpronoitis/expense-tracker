package com.app.expensetracker.dto.request;

import com.app.expensetracker.enumeration.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseUpdateRequestDTO {

    private LocalDate date;
    private BigDecimal amount;
    private String description;
    private PaymentMethod paymentMethod;
    private String categoryName;
}
