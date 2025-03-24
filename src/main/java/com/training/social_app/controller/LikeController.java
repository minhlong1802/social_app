package com.training.social_app.controller;

import com.training.social_app.dto.response.APIResponse;
import com.training.social_app.entity.Like;
import com.training.social_app.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@Slf4j
public class LikeController {
    private final LikeService likeService;

    //Like a post
    @Operation(summary = "Like a post")
    @PostMapping()
    public ResponseEntity<Object> likePost(@RequestParam String postId) {
        try {
            int id = Integer.parseInt(postId);
            if(id <= 0) {
                return APIResponse.responseBuilder(
                        null,
                        "Post id must be greater than 0",
                        HttpStatus.BAD_REQUEST
                );
            }
            Like like = likeService.likePost(id);
            return APIResponse.responseBuilder(like, "Post liked successfully", HttpStatus.OK);
        }catch (NumberFormatException e) {
            return APIResponse.responseBuilder(
                    null,
                    "Invalid postId. It must be an integer.",
                    HttpStatus.BAD_REQUEST
            );
        } catch (RuntimeException e) {
            log.error("Error likePost", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }catch (Exception e) {
            log.error("Error likePost", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    //Get all likes for a post
    @Operation(summary = "Get all likes for a post")
    @GetMapping("/post")
    public ResponseEntity<Object> getLikesForPost(@RequestParam String postId, @RequestParam(defaultValue = "1") Integer pageNo,  @RequestParam(defaultValue = "10") Integer pageSize) {
        try{
            int id = Integer.parseInt(postId);
            if(id <= 0) {
                return APIResponse.responseBuilder(
                        null,
                        "Post id must be greater than 0",
                        HttpStatus.BAD_REQUEST
                );
            }
            return APIResponse.responseBuilder(
                    likeService.getLikesForPost(id, pageNo, pageSize),
                    "Likes retrieved successfully",
                    HttpStatus.OK
            );
        }catch (NumberFormatException e) {
            return APIResponse.responseBuilder(
                    null,
                    "Invalid postId. It must be an integer.",
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error getLikesForPost", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    //Get like by id
    @Operation(summary = "Get like by id")
    @GetMapping("/{likeId}")
    public ResponseEntity<Object> getLikeById(@PathVariable String likeId) {
        try{
            int id = Integer.parseInt(likeId);
            if(id <= 0) {
                return APIResponse.responseBuilder(
                        null,
                        "Like id must be greater than 0",
                        HttpStatus.BAD_REQUEST
                );
            }
            return APIResponse.responseBuilder(
                    likeService.getLikeById(id),
                    "Like retrieved successfully",
                    HttpStatus.OK
            );
        }catch (NumberFormatException e) {
            return APIResponse.responseBuilder(
                    null,
                    "Invalid likeId. It must be an integer.",
                    HttpStatus.BAD_REQUEST
            );
        } catch (EntityNotFoundException e) {
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error getLikeById", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

//    //Count likes for a user in the past week
//    @GetMapping("/count")
//    public ResponseEntity<Object> countLikesForUserInPastWeek() {
//        try{
//            return APIResponse.responseBuilder(
//                    Collections.singletonMap("Last week's like count: ", likeService.countLikesForUserInPastWeek()),
//                    "Likes counted successfully",
//                    HttpStatus.OK
//            );
//        } catch (RuntimeException e) {
//            log.error("Error countLikesForUserInPastWeek", e);
//            return APIResponse.responseBuilder(
//                    null,
//                    Objects.requireNonNull(e.getMessage()),
//                    HttpStatus.BAD_REQUEST
//            );
//        } catch (Exception e) {
//            log.error("Error countLikesForUserInPastWeek", e);
//            return APIResponse.responseBuilder(
//                    null,
//                    "An unexpected error occurred",
//                    HttpStatus.INTERNAL_SERVER_ERROR
//            );
//        }
//    }
}
