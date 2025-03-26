package com.training.social_app.dto.response;

import com.training.social_app.enums.RequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendShipResponse {
    private Integer id;
    private Integer user1Id;
    private Integer user2Id;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
