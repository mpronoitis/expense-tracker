package com.app.expensetracker.mapper;

import com.app.expensetracker.domain.Category;
import com.app.expensetracker.dto.CategoryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper extends EntityMapper<CategoryDTO, Category> {
}
