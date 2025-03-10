package com.training.social_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LikeRequestDto {
    @NotNull(message = "Post id is mandatory")
    @NotBlank(message = "Post id is mandatory")
    private Integer postId;

    @NotNull(message = "User id is mandatory")
    @NotBlank(message = "User id is mandatory")
    private Integer userId;
}
