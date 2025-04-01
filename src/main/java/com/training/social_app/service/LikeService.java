package com.training.social_app.service;

import com.training.social_app.dto.response.LikeResponse;

import java.util.Map;

public interface LikeService {
    LikeResponse likePost(Integer postId);

    //Get all likes for a post
    Map<String, Object> getLikesForPost(Integer postId, Integer page, Integer size);

    //Get like by id
    LikeResponse getLikeById(Integer likeId);
}
