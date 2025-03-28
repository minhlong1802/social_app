package com.training.social_app.dto.response;

import com.training.social_app.enums.RequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendShipRequestResponse {
    private Integer id;
    private Integer userId;
    private String userFullName;
    private String userAvatarUrl;
    private RequestStatus status;
    private LocalDateTime createdAt;
}
