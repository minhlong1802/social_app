package com.training.social_app.service;

import com.training.social_app.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    Post createPost(String content, MultipartFile file);
    Post updatePost(String content, MultipartFile file ,Integer postId);
    void deletePost(Integer postId);
    List<Post> getPostsByUserId();
    List<Post> getPostsOfFriendsSortedByDate();
    int countPostsForUserInPastWeek();
    List<Post> findAll();
}
