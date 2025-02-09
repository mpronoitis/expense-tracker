package com.app.expensetracker.mapper;

import com.app.expensetracker.domain.Budget;
import com.app.expensetracker.dto.response.BudgetResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface BudgetMapper extends EntityMapper<BudgetResponseDTO, Budget> {
}
