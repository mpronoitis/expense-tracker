package com.app.expensetracker.service;

import com.app.expensetracker.domain.user.User;
import com.app.expensetracker.dto.UserClaimsDTO;
import com.app.expensetracker.error.exception.GenericBadRequestException;
import com.app.expensetracker.repository.UserRepository;
import com.app.expensetracker.shared.rest.enumeration.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserClaimsService implements UserDetailsService {

    private final UserRepository userRepository;


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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.getUserClaimsDTOByUser(userRepository.findByUsername(username).orElseThrow(() -> new GenericBadRequestException("User not found", ErrorType.IM_USER_NOT_FOUND)));
    }
}
