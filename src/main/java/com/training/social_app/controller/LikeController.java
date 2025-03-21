package com.training.social_app.controller;

import com.training.social_app.dto.response.APIResponse;
import com.training.social_app.entity.Like;
import com.training.social_app.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Objects;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@Slf4j
public class LikeController {
    private final LikeService likeService;

    //Like a post
    @PostMapping("/like")
    public ResponseEntity<Object> likePost(@RequestParam Integer postId) {
        try {
            Like like = likeService.likePost(postId);
            return APIResponse.responseBuilder(like, "Post liked successfully", HttpStatus.OK);
        }catch (RuntimeException e) {
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

    //Unlike a post
    @PostMapping("/unlike")
    public ResponseEntity<Object> unlikePost(@RequestParam Integer postId) {
        try{
            likeService.unlikePost(postId);
            return APIResponse.responseBuilder(
                    null,
                    "Post unliked successfully",
                    HttpStatus.OK
            );
        } catch (RuntimeException e) {
            log.error("Error unlikePost", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error unlikePost", e);
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
