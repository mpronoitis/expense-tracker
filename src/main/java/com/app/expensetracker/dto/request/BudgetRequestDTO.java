package com.app.expensetracker.dto.request;

import com.app.expensetracker.error.exception.GenericBadRequestException;
import com.app.expensetracker.shared.rest.enumeration.ErrorType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
public class BudgetRequestDTO {

    @NotNull(message = "Startdate can not be null")
    private LocalDate startDate;
    @NotNull(message = "Enddate can not be null")
    private LocalDate endDate;
    @NotNull(message = "LimitAmount can not be null")
    private BigDecimal limitAmount;
    @NotNull(message = "Category can not be null")
    private String categoryName;

    public void validate() {

        // Start Date can't be in the past
        if (startDate.isBefore(LocalDate.now())) {
            throw new GenericBadRequestException("Start date cannot be in the past", ErrorType.IM_INVALID_DATE);
        }

        // Start Date can't be after End Date
        if (startDate.isAfter(endDate)) {
            throw new GenericBadRequestException("Start date cannot be after the end date", ErrorType.IM_INVALID_DATE_RANGE);
        }

        // Validate maximum budget duration (e.g., 1 year max)
        if (startDate.until(endDate, ChronoUnit.DAYS) > 365) {
            throw new GenericBadRequestException("Budget duration cannot exceed one year", ErrorType.IM_INVALID_DATE_RANGE);
        }

    }
}
