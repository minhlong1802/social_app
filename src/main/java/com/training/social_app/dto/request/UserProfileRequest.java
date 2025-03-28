package com.training.social_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileRequest {
    @NotNull(message = "Full name is required")
    @NotBlank(message = "Full name is required")
    @Size(max = 50, message = "Full name must be less than 50 characters")
    private String fullName;
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Invalid date format. Use yyyy-MM-dd")
    private String birthDate;
    @Size(max = 50, message = "Occupation must be less than 50 characters")
    private String occupation;
    @Size(max = 50, message = "Location must be less than 50 characters")
    private String location;
}
