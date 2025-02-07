package com.app.expensetracker.dto.response;

import com.app.expensetracker.dto.CategoryDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BudgetResponseDTO {
    private BigDecimal limitAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private CategoryDTO category;
}
