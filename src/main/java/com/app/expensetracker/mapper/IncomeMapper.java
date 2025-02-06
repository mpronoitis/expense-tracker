package com.app.expensetracker.mapper;

import com.app.expensetracker.domain.Income;
import com.app.expensetracker.dto.response.IncomeResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IncomeMapper extends EntityMapper<IncomeResponseDTO, Income> {
}
