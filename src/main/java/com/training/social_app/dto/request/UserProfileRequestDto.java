package com.training.social_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileRequestDto {
    @NotBlank(message = "Full name is required")
    @NotNull(message = "Full name is required")
    private String fullName;
    @NotNull(message = "Birth date is required")
    @NotBlank(message = "Birth date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Invalid date format. Use yyyy-MM-dd")
    private String birthDate;
    private String occupation;
    private String location;
    private String avatarUrl;
}
