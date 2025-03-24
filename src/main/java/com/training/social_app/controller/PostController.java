package com.training.social_app.controller;

import com.training.social_app.dto.request.DeleteRequest;
import com.training.social_app.dto.response.APIResponse;
import com.training.social_app.dto.response.PostResponse;
import com.training.social_app.entity.Post;
import com.training.social_app.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
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
    @Operation(summary = "Get posts of friends sorted by date")
    @GetMapping
    public ResponseEntity<Object> getPostsOfFriendsSortedByDate(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            List<PostResponse> posts = postService.getPostsOfFriendsSortedByDate(pageNo, pageSize);
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
    @Operation(summary = "Get posts by user id")
    @GetMapping("/user")
    public ResponseEntity<Object> getPostsByUserId() {
        try {
            List<PostResponse> posts = postService.getPostsByUserId();
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
    @Operation(summary = "Create post")
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Object> createPost(@RequestPart(required = false) String content,
                                             @RequestPart(name = "imageUrl", required = false) MultipartFile file) {
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
    @Operation(summary = "Update post")
    @PutMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Object> updatePost(@RequestPart(required = false) String content,
                                             @RequestPart(name = "imageUrl", required = false) MultipartFile file,
                                             @RequestPart String postId) {
        try {
            int id = Integer.parseInt(postId);
            if(id <= 0) {
                return APIResponse.responseBuilder(
                        null,
                        "Post id must be greater than 0",
                        HttpStatus.BAD_REQUEST
                );
            }
            Post post = postService.updatePost(content, file, id);
            return APIResponse.responseBuilder(post, "Post updated successfully", HttpStatus.OK);
        } catch (NumberFormatException e) {
            return APIResponse.responseBuilder(
                    null,
                    "Invalid postId. It must be an integer.",
                    HttpStatus.BAD_REQUEST
            );
        }catch (RuntimeException e) {
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
    @Operation(summary = "Delete post")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Object> deletePost(@PathVariable String postId) {
        try {
            int id = Integer.parseInt(postId);
            if(id <= 0) {
                return APIResponse.responseBuilder(
                        null,
                        "Post id must be greater than 0",
                        HttpStatus.BAD_REQUEST
                );            }
            postService.deletePost(id);
            return APIResponse.responseBuilder(null, "Post deleted successfully", HttpStatus.OK);
        } catch (NumberFormatException e) {
            return APIResponse.responseBuilder(
                    null,
                    "Invalid postId. It must be an integer.",
                    HttpStatus.BAD_REQUEST
            );
        } catch (EntityNotFoundException e) {
            log.error("Error deletePost", e);
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
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

    // Find all posts
    @Operation(summary = "Find all posts (admin)")
    @GetMapping("/all")
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "1") Integer pageNo,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            return APIResponse.responseBuilder(
                    postService.findAll(pageNo, pageSize),
                    "Posts retrieved successfully",
                    HttpStatus.OK
            );
        } catch (EntityNotFoundException e) {
            log.error("Error findAll", e);
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        } catch (RuntimeException e) {
            log.error("Error findAll", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error findAll", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Delete posts
    @Operation(summary = "Delete posts (admin)")
    @DeleteMapping("/delete-posts")
    public ResponseEntity<?> deletePosts(@RequestBody DeleteRequest request) {
        try {
            postService.deletePosts(request);
            return APIResponse.responseBuilder(
                    null,
                    "Posts deleted successfully",
                    HttpStatus.OK
            );
        } catch (EntityNotFoundException e) {
            log.error("Error deletePosts", e);
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        } catch (RuntimeException e) {
            log.error("Error deletePosts", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error deletePosts", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    //Get post by Id
    @Operation(summary = "Get post by Id")
    @GetMapping("/{postId}")
    public ResponseEntity<Object> getPostById(@PathVariable String postId) {
        try {
            int id = Integer.parseInt(postId);
            if(id <= 0) {
                return APIResponse.responseBuilder(
                        null,
                        "Post id must be greater than 0",
                        HttpStatus.BAD_REQUEST
                );
            }
            PostResponse post = postService.findById(id);
            return APIResponse.responseBuilder(post, "Post retrieved successfully", HttpStatus.OK);
        } catch (NumberFormatException e) {
            return APIResponse.responseBuilder(
                    null,
                    "Invalid postId. It must be an integer.",
                    HttpStatus.BAD_REQUEST
            );
        } catch (EntityNotFoundException e) {
            log.error("Error getPostById", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error getPostById", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
