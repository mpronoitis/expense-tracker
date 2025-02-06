package com.app.expensetracker.mapper;

import com.app.expensetracker.domain.Expense;
import com.app.expensetracker.dto.response.ExpenseResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ExpenseMapper extends EntityMapper<ExpenseResponseDTO, Expense> {
}
