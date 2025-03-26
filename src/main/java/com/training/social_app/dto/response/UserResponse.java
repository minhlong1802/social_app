package com.training.social_app.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserResponse {
    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private LocalDate birthDate;
    private String occupation;
    private String location;
    private String avatarUrl;
}
