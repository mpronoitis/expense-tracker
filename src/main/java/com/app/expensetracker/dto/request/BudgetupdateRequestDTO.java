package com.app.expensetracker.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Data
public class BudgetupdateRequestDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    @PositiveOrZero //it is optional, but if provided must greater than zero
    private BigDecimal limitAmount;

    public void validate() {

        //Ensure that startDate and endDate are in the same month
        YearMonth startMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);

        if (!startMonth.equals(endMonth)) {
            throw new IllegalArgumentException("Start date and end date must be in the same month");
        }

        if (!endDate.equals(endMonth.atEndOfMonth())) {
            throw new IllegalArgumentException("End date must be the last day of the month");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before the end date");
        }
    }
}
