package com.training.social_app.service;

import com.training.social_app.dto.request.CommentRequest;
import com.training.social_app.entity.Comment;

import java.util.List;

public interface CommentService {
    Comment commentPost(CommentRequest commentRequest);
    Comment editComment(CommentRequest commentRequest, Integer commentId);
    void deleteComment(Integer postId);
    List<Comment> getCommentsByPostId(Integer postId);
    int countCommentsForUserInPastWeek();
}
