package com.training.social_app.service;

import com.training.social_app.dto.request.CommentRequest;
import com.training.social_app.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse commentPost(CommentRequest commentRequest);
    CommentResponse editComment(CommentRequest commentRequest, Integer commentId);
    void deleteComment(Integer postId);
    List<CommentResponse> getCommentsByPostId(Integer postId, Integer page, Integer size);
    CommentResponse getCommentById(Integer commentId);
}
