package com.training.social_app.service;

import com.training.social_app.entity.Like;

public interface LikeService {
    Like likePost(Integer postId);

    int countLikesForUserInPastWeek();

    //Remove the like
    void unlikePost(Integer postId);
}
