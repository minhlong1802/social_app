package com.training.social_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @NotNull(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
