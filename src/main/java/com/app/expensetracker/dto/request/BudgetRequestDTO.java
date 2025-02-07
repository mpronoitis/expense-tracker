package com.app.expensetracker.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Data
public class BudgetRequestDTO {

    @NotNull(message = "Startdate can not be null")
    @FutureOrPresent
    private LocalDate startDate;
    @NotNull(message = "Enddate can not be null")
    private LocalDate endDate;
    @NotNull(message = "LimitAmount can not be null")
    private BigDecimal limitAmount;
    @NotNull(message = "Category can not be null")
    private String categoryName;

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
    }
}
