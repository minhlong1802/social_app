package com.training.social_app.controller;

import com.training.social_app.dto.request.CommentRequest;
import com.training.social_app.dto.response.APIResponse;
import com.training.social_app.entity.Comment;
import com.training.social_app.service.CommentService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//http://localhost:8080/swagger-ui/index.html
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    // Comment a post
    @Operation(summary = "Comment a post")
    @PostMapping()
    public ResponseEntity<Object> commentPost(@RequestBody @Valid CommentRequest commentRequest, BindingResult bindingResult) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (bindingResult.hasErrors()) {
                bindingResult.getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
            }
            if (!errors.isEmpty()) {
                return APIResponse.responseBuilder(
                        errors,
                        "Validation failed",
                        HttpStatus.BAD_REQUEST
                );
            }
            Comment comment = commentService.commentPost(commentRequest);
            return APIResponse.responseBuilder(comment, "Post commented successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error commentPost", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error commentPost", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Edit a comment
    @Operation(summary = "Edit a comment")
    @PutMapping("/{commentId}")
    public ResponseEntity<Object> editComment(@RequestBody @Valid CommentRequest commentRequest, @PathVariable String commentId, BindingResult bindingResult) {
        try {
            int id = Integer.parseInt(commentId);
            if(id<=0){
                return APIResponse.responseBuilder(
                        null,
                        "Comment id must be greater than 0",
                        HttpStatus.BAD_REQUEST
                );
            }
            Map<String, String> errors = new HashMap<>();
            if (bindingResult.hasErrors()) {
                bindingResult.getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
            }
            if (!errors.isEmpty()) {
                return APIResponse.responseBuilder(
                        errors,
                        "Validation failed",
                        HttpStatus.BAD_REQUEST
                );
            }
            Comment comment = commentService.editComment(commentRequest, id);
            return APIResponse.responseBuilder(comment, "Comment edited successfully", HttpStatus.OK);
        }catch (NumberFormatException e) {
            return APIResponse.responseBuilder(
                    null,
                    "Invalid postId. It must be an integer.",
                    HttpStatus.BAD_REQUEST
            );
        }  catch (RuntimeException e) {
            log.error("Error editComment", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error editComment", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Delete a comment
    @Operation(summary = "Delete a comment")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable String commentId) {
        try {
            int id = Integer.parseInt(commentId);
            if(id<=0){
                return APIResponse.responseBuilder(
                        null,
                        "Comment id must be greater than 0",
                        HttpStatus.BAD_REQUEST
                );
            }
            commentService.deleteComment(id);
            return APIResponse.responseBuilder(null, "Comment deleted successfully", HttpStatus.OK);
        } catch (NumberFormatException e) {
            return APIResponse.responseBuilder(
                    null,
                    "Invalid postId. It must be an integer.",
                    HttpStatus.BAD_REQUEST
            );
        } catch (RuntimeException e) {
            log.error("Error deleteComment", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error deleteComment", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Get comments by post id
    @Operation(summary = "Get comments by post id")
    @GetMapping("/post/{postId}")
    public ResponseEntity<Object> getCommentsByPostId(@PathVariable String postId) {
        try {
            int id = Integer.parseInt(postId);
            if(id <= 0) {
                return APIResponse.responseBuilder(
                        null,
                        "Post id must be greater than 0",
                        HttpStatus.BAD_REQUEST
                );
            }
            return APIResponse.responseBuilder(
                    commentService.getCommentsByPostId(id),
                    "Comments retrieved successfully",
                    HttpStatus.OK
            );
        }catch (NumberFormatException e) {
            return APIResponse.responseBuilder(
                    null,
                    "Invalid postId. It must be an integer.",
                    HttpStatus.BAD_REQUEST
            );
        }  catch (EntityNotFoundException e) {
            log.error("Error getCommentsByPostId", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            log.error("Error getCommentsByPostId", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    //Get comment by id
    @Operation(summary = "Get comment by id")
    @GetMapping("/{commentId}")
    public ResponseEntity<Object> getCommentById(@PathVariable String commentId) {
        try {
            int id = Integer.parseInt(commentId);
            if(id <= 0) {
                return APIResponse.responseBuilder(
                        null,
                        "Comment id must be greater than 0",
                        HttpStatus.BAD_REQUEST
                );
            }
            Comment comment = commentService.getCommentById(id);
            return APIResponse.responseBuilder(comment, "Comment retrieved successfully", HttpStatus.OK);
        } catch (NumberFormatException e) {
            return APIResponse.responseBuilder(
                    null,
                    "Invalid commentId. It must be an integer.",
                    HttpStatus.BAD_REQUEST
            );
        } catch (EntityNotFoundException e) {
            log.error("Error getCommentById", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            log.error("Error getCommentById", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

//    // Count comments for a user in the past week
//    @GetMapping("/count")
//    public ResponseEntity<Object> countCommentsForUserInPastWeek() {
//        try {
//            return APIResponse.responseBuilder(
//                    commentService.countCommentsForUserInPastWeek(),
//                    "Comments counted successfully",
//                    HttpStatus.OK
//            );
//        } catch (RuntimeException e) {
//            log.error("Error countCommentsForUserInPastWeek", e);
//            return APIResponse.responseBuilder(
//                    null,
//                    Objects.requireNonNull(e.getMessage()),
//                    HttpStatus.BAD_REQUEST
//            );
//        } catch (Exception e) {
//            log.error("Error countCommentsForUserInPastWeek", e);
//            return APIResponse.responseBuilder(
//                    null,
//                    "An unexpected error occurred",
//                    HttpStatus.INTERNAL_SERVER_ERROR
//            );
//        }
//    }
}
