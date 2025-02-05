package com.app.expensetracker.service;

import com.app.expensetracker.config.JwtTokenUtil;
import com.app.expensetracker.dto.UserClaimsDTO;
import com.app.expensetracker.dto.request.AuthRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenUtil jwtTokenUtil;
    private final DaoAuthenticationProvider authenticationProvider;
    public String login(AuthRequestDTO authRequestDTO) {
        UserClaimsDTO userClaimsDTO;
        try {
            Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
            userClaimsDTO = (UserClaimsDTO) authentication.getPrincipal();
        } catch (BadCredentialsException ex) {
            throw ex;
        }

        String token = jwtTokenUtil.generateAccessToken(userClaimsDTO);
        return token;

    }
}
