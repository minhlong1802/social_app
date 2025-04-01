package com.training.social_app.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private Integer id;
    private String fullName;
    private String avatarUrl;
}
