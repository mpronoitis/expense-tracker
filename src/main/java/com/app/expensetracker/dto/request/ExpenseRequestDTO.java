package com.app.expensetracker.dto.request;

import com.app.expensetracker.enumeration.PaymentMethod;
import com.app.expensetracker.error.exception.GenericBadRequestException;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseRequestDTO {
    @NotNull(message = "Date can not be null")
    private LocalDate date;
    @NotNull(message = "Amount can not be null")
    private BigDecimal amount;
    @NotNull(message = "Description can not be null")
    private String description;
    @NotNull(message = "PaymentMethod can not be null")
    private PaymentMethod paymentMethod;
    @NotNull(message = "Category can not be null")
    private String categoryName;

    public void validate() {
        if (date.isAfter(LocalDate.now())) {
            throw new GenericBadRequestException("Expense date can not be in the future");
        }
    }
}
