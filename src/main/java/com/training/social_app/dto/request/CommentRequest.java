package com.training.social_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CommentRequest {
    @NotNull(message = "content is required")
    @NotBlank(message = "content is required")
    private String content;
    @NotNull(message = "postId is required")
    @NotBlank(message = "postId is required")
    @Positive(message = "postId must be positive number")
    private Integer postId;
}
