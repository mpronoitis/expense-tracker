package com.app.expensetracker.controller;

import com.app.expensetracker.dto.request.RegisteringUserRequestDTO;
import com.app.expensetracker.dto.response.RegisteringUserResponseDTO;
import com.app.expensetracker.service.RegistrationService;
import com.app.expensetracker.shared.rest.model.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/public/registration/sign-up")
    public ApiResponse<RegisteringUserResponseDTO> signUp(@RequestBody @Valid RegisteringUserRequestDTO userRequestDTO) {
        return new ApiResponse.Builder<RegisteringUserResponseDTO>().payload(registrationService.registerUser(userRequestDTO)).build();
    }

}
