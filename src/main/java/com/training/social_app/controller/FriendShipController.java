package com.training.social_app.controller;

import com.training.social_app.dto.response.APIResponse;
import com.training.social_app.entity.FriendShip;
import com.training.social_app.entity.User;
import com.training.social_app.service.FriendShipService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
@Slf4j
public class FriendShipController {
    private final FriendShipService friendShipService;

    // Get friends of user
    @GetMapping
    public ResponseEntity<Object> getFriendsOfUser() {
        try {
            List<User> friends = friendShipService.getFriends();
            return APIResponse.responseBuilder(friends, "Friends retrieved successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error getFriendsOfUser", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error getFriendsOfUser", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Get friend requests of user
    @GetMapping("/requests")
    public ResponseEntity<Object> getFriendRequestsOfUser() {
        try {
            List<User> friendRequests = friendShipService.getFriendRequests();
            return APIResponse.responseBuilder(friendRequests, "Friend requests retrieved successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            log.error("Error getFriendRequestsOfUser", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error getFriendRequestsOfUser", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Get friend requests to user
    @GetMapping("/requests/to")
    public ResponseEntity<Object> getFriendRequestsToUser() {
        try {
            List<User> friendRequestsToUser = friendShipService.getFriendRequestsToUser();
            return APIResponse.responseBuilder(friendRequestsToUser, "Friend requests to user retrieved successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            log.error("Error getFriendRequestsToUser", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error getFriendRequestsToUser", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    //Get friendship by friend id
    @GetMapping("/friendId")
    public ResponseEntity<Object> getFriendshipByFriendId(@PathVariable Integer friendId) {
        try {
            FriendShip friendShip = friendShipService.getFriendship(friendId);
            return APIResponse.responseBuilder(friendShip, "Friendship retrieved successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error getFriendshipByFriendId", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error getFriendshipByFriendId", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    //Send friend request
    @GetMapping("/sendRequest")
    public ResponseEntity<Object> sendFriendRequest(@PathVariable Integer requesteeId) {
        try {
            friendShipService.sendFriendRequest(requesteeId);
            return APIResponse.responseBuilder(null, "Friend request sent successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error sendFriendRequest", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error sendFriendRequest", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    //Accept friend request
    @GetMapping("/acceptRequest")
    public ResponseEntity<Object> acceptFriendRequest(@PathVariable Integer requestId) {
        try {
            friendShipService.acceptFriendRequest(requestId);
            return APIResponse.responseBuilder(null, "Friend request accepted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error acceptFriendRequest", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error acceptFriendRequest", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    //Reject friend request
    @GetMapping("/rejectRequest")
    public ResponseEntity<Object> rejectFriendRequest(@PathVariable Integer requestId) {
        try {
            friendShipService.rejectFriendRequest(requestId);
            return APIResponse.responseBuilder(null, "Friend request rejected successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error rejectFriendRequest", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error rejectFriendRequest", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    //Unfriend
    @GetMapping("/unfriend")
    public ResponseEntity<Object> unfriend(@PathVariable Integer friendId) {
        try {
            friendShipService.unfriend(friendId);
            return APIResponse.responseBuilder(null, "Unfriended successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error unfriend", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error unfriend", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

//    //Count friends in past week
//    @GetMapping("/count")
//    public ResponseEntity<Object> countFriendsInPastWeek() {
//        try {
//            int count = friendShipService.countFriendsInPastWeek();
//            return APIResponse.responseBuilder(count, "Friends count retrieved successfully", HttpStatus.OK);
//        } catch (RuntimeException e) {
//            log.error("Error countFriendsInPastWeek", e);
//            return APIResponse.responseBuilder(
//                    null,
//                    Objects.requireNonNull(e.getMessage()),
//                    HttpStatus.BAD_REQUEST
//            );
//        } catch (Exception e) {
//            log.error("Error countFriendsInPastWeek", e);
//            return APIResponse.responseBuilder(
//                    null,
//                    "An unexpected error occurred",
//                    HttpStatus.INTERNAL_SERVER_ERROR
//            );
//        }
//    }
}
