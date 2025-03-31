package com.training.social_app.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeResponse {
    private Integer id;
    private Integer postId;
    private Integer userId;
    private String userFullName;
    private String userProfileImage;
    private LocalDateTime createdAt;
}
