package com.training.social_app.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Integer id;
    private String fullName;
    private String avatarUrl;
}
