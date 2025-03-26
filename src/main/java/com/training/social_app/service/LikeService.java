package com.training.social_app.service;

import com.training.social_app.dto.response.LikeResponse;

import java.util.List;

public interface LikeService {
    LikeResponse likePost(Integer postId);

    //Get all likes for a post
    List<LikeResponse> getLikesForPost(Integer postId, Integer page, Integer size);

    //Get like by id
    LikeResponse getLikeById(Integer likeId);
}
