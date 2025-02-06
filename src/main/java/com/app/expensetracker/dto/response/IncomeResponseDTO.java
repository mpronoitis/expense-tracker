package com.app.expensetracker.dto.response;

import com.app.expensetracker.enumeration.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class IncomeResponseDTO {

    private LocalDate date;
    private BigDecimal amount;
    private String source;
    private PaymentMethod paymentMethod;
}
