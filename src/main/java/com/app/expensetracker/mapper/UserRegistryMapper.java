package com.app.expensetracker.mapper;

import com.app.expensetracker.domain.user.UserRegistry;
import com.app.expensetracker.dto.UserRegistryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRegistryMapper extends EntityMapper<UserRegistryDTO, UserRegistry> {
}
