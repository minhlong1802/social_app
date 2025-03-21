package com.training.social_app.dto.response;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;

@Data
public class UserResponse {
    private Integer id;
    private String username;
    private String email;
}
