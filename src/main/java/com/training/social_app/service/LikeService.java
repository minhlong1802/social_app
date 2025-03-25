package com.training.social_app.service;

import com.training.social_app.entity.Like;

import java.util.List;

public interface LikeService {
    Like likePost(Integer postId);

    //Get all likes for a post
    List<Like> getLikesForPost(Integer postId, Integer page, Integer size);

    //Get like by id
    Like getLikeById(Integer likeId);
}
