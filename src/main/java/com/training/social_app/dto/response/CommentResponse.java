package com.training.social_app.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private Integer id;
    private Integer postId;
    private Integer userId;
    private String userFullName;
    private String userProfileImage;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
