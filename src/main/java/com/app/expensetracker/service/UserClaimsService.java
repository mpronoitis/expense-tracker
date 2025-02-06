package com.app.expensetracker.service;

import com.app.expensetracker.domain.user.User;
import com.app.expensetracker.dto.UserClaimsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserClaimsService {


    public UserClaimsDTO getUserClaimsDTOByUser(User user) {
        UserClaimsDTO userClaimsDTO = new UserClaimsDTO();
        userClaimsDTO.setId(user.getId());
        userClaimsDTO.setUsername(user.getUsername());
        userClaimsDTO.setPassword(user.getPassword());
        userClaimsDTO.setFirstName(user.getUserRegistry().getFirstName());
        userClaimsDTO.setLastName(user.getUserRegistry().getLastName());

        return userClaimsDTO;
    }

    public static UserClaimsDTO getUserClaimsDTO() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserClaimsDTO) authentication.getPrincipal();
    }
}
