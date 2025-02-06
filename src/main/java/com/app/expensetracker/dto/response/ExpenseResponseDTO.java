package com.app.expensetracker.dto.response;

import com.app.expensetracker.dto.CategoryDTO;
import com.app.expensetracker.enumeration.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseResponseDTO {

    private LocalDate date;
    private BigDecimal amount;
    private String description;
    private PaymentMethod paymentMethod;
    private CategoryDTO category;
}
