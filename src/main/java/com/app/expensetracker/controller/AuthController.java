package com.app.expensetracker.controller;

import com.app.expensetracker.dto.request.AuthRequestDTO;
import com.app.expensetracker.service.AuthService;
import com.app.expensetracker.shared.rest.model.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Login to the application", description = "Return a token associated with the user")
    @PostMapping("/public/auth/authenticate")
    public ApiResponse<String> authenticate(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        return new ApiResponse.Builder<String>().payload(authService.login(authRequestDTO)).build();
    }

}
