package com.training.social_app.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DeleteRequest {
    // Getter and Setter
    private List<Integer> ids;

}