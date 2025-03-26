package com.training.social_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserProfileRequest {
    private String fullName;
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Invalid date format. Use yyyy-MM-dd")
    private String birthDate;
    private String occupation;
    private String location;
}
