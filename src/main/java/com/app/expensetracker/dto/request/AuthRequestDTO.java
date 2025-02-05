package com.app.expensetracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequestDTO {

    @NotBlank
    @Email
    @Size(max = 100)
    private String username;
    @NotBlank
    @Size(max = 100)
    private String password;
}
