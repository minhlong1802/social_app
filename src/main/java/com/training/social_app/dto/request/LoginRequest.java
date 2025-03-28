package com.training.social_app.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @NotNull(message = "Username is required")
    @Size(max = 50, message = "Username must be less than 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @NotNull(message = "Password is required")
    @Size(max = 50, message = "Password must be less than 50 characters")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
