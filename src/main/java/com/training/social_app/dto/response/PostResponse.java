package com.training.social_app.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {
    private Integer id;
    private String content;
    private String imageUrl;
    private Integer userId;
    private String userFullName;
    private String userProfileImage;
    private boolean isEdited;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
