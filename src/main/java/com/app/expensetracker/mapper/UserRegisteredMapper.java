package com.app.expensetracker.mapper;

import com.app.expensetracker.domain.user.User;
import com.app.expensetracker.dto.response.RegisteringUserResponseDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = UserRegistryMapper.class)
public interface UserRegisteredMapper extends EntityMapper<RegisteringUserResponseDTO, User>{
}
