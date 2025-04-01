package com.training.social_app.service;

import com.training.social_app.dto.request.DeleteRequest;
import com.training.social_app.dto.response.PostResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface PostService {
    PostResponse createPost(String content, MultipartFile file);
    PostResponse updatePost(String content, MultipartFile file ,Integer postId);
    void deletePost(Integer postId);
    Map<String, Object> getPostsByUserId(Integer page, Integer size);
    Map<String, Object> getPostsOfFriendsSortedByDate(Integer page, Integer size);
    Map<String, Object> findAll(Integer page, Integer size);
    void deletePosts(DeleteRequest deleteRequest);
    PostResponse findById(Integer postId);
}
