package com.training.social_app.service;

import com.training.social_app.dto.request.DeleteRequest;
import com.training.social_app.dto.response.PostResponse;
import com.training.social_app.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    PostResponse createPost(String content, MultipartFile file);
    PostResponse updatePost(String content, MultipartFile file ,Integer postId);
    void deletePost(Integer postId);
    List<PostResponse> getPostsByUserId(Integer page, Integer size);
    List<PostResponse> getPostsOfFriendsSortedByDate(Integer page, Integer size);
    List<PostResponse> findAll(Integer page, Integer size);
    void deletePosts(DeleteRequest deleteRequest);
    PostResponse findById(Integer postId);
}
