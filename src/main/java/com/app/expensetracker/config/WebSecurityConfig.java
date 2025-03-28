package com.app.expensetracker.config;


import com.app.expensetracker.service.UserClaimsService;
import com.app.expensetracker.shared.rest.enumeration.ErrorType;
import com.app.expensetracker.shared.rest.model.ApiResponse;
import com.app.expensetracker.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
@Slf4j

public class WebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final UserClaimsService userClaimsService;


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //define public URLS
          String[] URLS = {
                "/public/**",
                "/v3/api-docs/**",
                "/swagger-ui/index.html",
                "/swagger-ui/**"
        };
        // Enable CORS and disable CSRF
        http.cors().and().csrf().disable();

        // Set session management to stateless because we will use jwt-token based authentication
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        ///handler for unauthorized requests
        http.exceptionHandling().authenticationEntryPoint(this::handleNotFound);

        //set the authenticationProvider
        http.authenticationProvider(authenticationProvider());
        // Authorize all request from public/*
        http.authorizeHttpRequests(authorize ->
                authorize
                        .requestMatchers(URLS)
                        .permitAll()
                        .anyRequest()
                        .authenticated());

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //to hash user passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
      //  daoAuthenticationProvider.setUserDetailsService((username -> userClaimsService.getUserClaimsDTOByUser(userRepository.findByUsername(username).orElseThrow(() -> new GenericBadRequestException("User not found", ErrorType.IM_USER_NOT_FOUND))) ));
        daoAuthenticationProvider.setUserDetailsService(userClaimsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    private void handleNotFound(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ApiResponse<Null> unauthorizedResponse =
                new ApiResponse.Builder<Null>(ErrorType.IM_GENERIC.getCode(), false)
                        .errorMessage(ErrorType.IM_GENERIC.getMessage())
                        .build();
        Utils.changeResponse(response, HttpStatus.BAD_REQUEST.value(), unauthorizedResponse);
    }
}
