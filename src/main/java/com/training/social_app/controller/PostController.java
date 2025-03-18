package com.training.social_app.controller;

import com.training.social_app.dto.response.APIResponse;
import com.training.social_app.entity.Post;
import com.training.social_app.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;

    // Get posts of friends sorted by date
    @GetMapping
    public ResponseEntity<Object> getPostsOfFriendsSortedByDate() {
        try {
            List<Post> posts = postService.getPostsOfFriendsSortedByDate();
            return APIResponse.responseBuilder(posts, "Posts retrieved successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error getPostsOfFriendsSortedByDate", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error getPostsOfFriendsSortedByDate", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Get posts by user id
    @GetMapping("/user")
    public ResponseEntity<Object> getPostsByUserId() {
        try {
            List<Post> posts = postService.getPostsByUserId();
            return APIResponse.responseBuilder(posts, "Posts retrieved successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error getPostsByUserId", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error getPostsByUserId", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

//    @GetMapping("/count")
//    public ResponseEntity<Object> countPostsForUserInPastWeek() {
//        try {
//            int count = postService.countPostsForUserInPastWeek();
//            return APIResponse.responseBuilder(count, "Posts count retrieved successfully", HttpStatus.OK);
//        } catch (RuntimeException e) {
//            log.error("Error countPostsForUserInPastWeek", e);
//            return APIResponse.responseBuilder(
//                    null,
//                    Objects.requireNonNull(e.getMessage()),
//                    HttpStatus.BAD_REQUEST
//            );
//        } catch (Exception e) {
//            log.error("Error countPostsForUserInPastWeek occurred", e);
//            return APIResponse.responseBuilder(
//                    null,
//                    "An unexpected error occurred",
//                    HttpStatus.INTERNAL_SERVER_ERROR
//            );
//        }
//    }

    // Create post
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Object> createPost(@RequestPart(required = false) String content,
                                             @RequestPart(name = "file", required = false) MultipartFile file) {
        try {
            Post post = postService.createPost(content,file);
            return APIResponse.responseBuilder(post, "Post created successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error createPost", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error createPost", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Update post
    @PutMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Object> updatePost(@RequestPart(required = false) String content,
                                             @RequestPart(name = "file", required = false) MultipartFile file,
                                             @RequestPart Integer postId) {
        try {
            Post post = postService.updatePost(content, file, postId);
            return APIResponse.responseBuilder(post, "Post updated successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error updatePost", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error updatePost", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Delete post
    @DeleteMapping("/{postId}")
    public ResponseEntity<Object> deletePost(@PathVariable Integer postId) {
        try {
            postService.deletePost(postId);
            return APIResponse.responseBuilder(null, "Post deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error deletePost", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error deletePost", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}
