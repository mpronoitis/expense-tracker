package com.app.expensetracker.dto.request;

import com.app.expensetracker.enumeration.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class IncomeRequestDTO {
    @NotNull(message = "Date can not be null")
    private LocalDate date;
    @NotNull(message = "Amount can not be null")
    private BigDecimal amount;
    @NotNull(message = "Source can not be null")
    private String source;
    @NotNull(message = "PaymentMethod can not be null")
    private PaymentMethod paymentMethod;
}
