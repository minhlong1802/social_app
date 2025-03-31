package com.training.social_app.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "Email is required")
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
//    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @NotNull(message = "Password is required")
    @Size(max = 50, message = "Password must be less than 50 characters")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
