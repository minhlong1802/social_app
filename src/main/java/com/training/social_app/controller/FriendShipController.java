package com.training.social_app.controller;

import com.training.social_app.dto.response.APIResponse;
import com.training.social_app.entity.FriendShip;
import com.training.social_app.entity.User;
import com.training.social_app.service.FriendShipService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
@Slf4j
public class FriendShipController {
    private final FriendShipService friendShipService;

    // Get friends of user
    @Operation(summary = "Get friends of user")
    @GetMapping
    public ResponseEntity<Object> getFriendsOfUser() {
        try {
            List<User> friends = friendShipService.getFriends();
            return APIResponse.responseBuilder(friends, "Friends retrieved successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            log.error("Error getFriendsOfUser", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.NOT_FOUND
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
    @Operation(summary = "Get friend requests of user")
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
                    HttpStatus.NOT_FOUND
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
    @Operation(summary = "Get friend requests to user")
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
                    HttpStatus.NOT_FOUND
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
    @Operation(summary = "Get friendship by friend id")
    @GetMapping("/{friendId}")
    public ResponseEntity<Object> getFriendshipByFriendId(@PathVariable String friendId) {
        try {
            int id = Integer.parseInt(friendId);
            if(id<=0) {
                return APIResponse.responseBuilder(
                        null,
                        "Friend id must be greater than 0",
                        HttpStatus.BAD_REQUEST
                );
            }
            FriendShip friendShip = friendShipService.getFriendship(id);
            return APIResponse.responseBuilder(friendShip, "Friendship retrieved successfully", HttpStatus.OK);
        }catch (NumberFormatException e) {
            return APIResponse.responseBuilder(
                    null,
                    "Invalid friendId. It must be an integer.",
                    HttpStatus.BAD_REQUEST
            );
        }  catch (EntityNotFoundException e) {
            log.error("Error getFriendshipByFriendId", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.NOT_FOUND
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
    @Operation(summary = "Send friend request")
    @PostMapping("/sendRequest/{requesteeId}")
    public ResponseEntity<Object> sendFriendRequest(@PathVariable String requesteeId) {
        try {
            int id = Integer.parseInt(requesteeId);
            if(id<=0) {
                return APIResponse.responseBuilder(
                        null,
                        "Requestee id must be greater than 0",
                        HttpStatus.BAD_REQUEST
                );            }
            friendShipService.sendFriendRequest(id);
            return APIResponse.responseBuilder(null, "Friend request sent successfully", HttpStatus.OK);
        }catch (NumberFormatException e) {
            return APIResponse.responseBuilder(
                    null,
                    "Invalid requesteeId. It must be an integer.",
                    HttpStatus.BAD_REQUEST
            );
        } catch (EntityNotFoundException e) {
            log.error("Error sendFriendRequest", e);
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
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
    @Operation(summary = "Accept friend request")
    @PutMapping("/acceptRequest/{requestId}")
    public ResponseEntity<Object> acceptFriendRequest(@PathVariable String requestId) {
        try {
            int id = Integer.parseInt(requestId);
            if(id<=0) {
                return APIResponse.responseBuilder(
                        null,
                        "Request id must be greater than 0",
                        HttpStatus.BAD_REQUEST
                );            }
            friendShipService.acceptFriendRequest(id);
            return APIResponse.responseBuilder(null, "Friend request accepted successfully", HttpStatus.OK);
        } catch (NumberFormatException e) {
            return APIResponse.responseBuilder(
                    null,
                    "Invalid requestId. It must be an integer.",
                    HttpStatus.BAD_REQUEST
            );
        } catch (EntityNotFoundException e) {
            log.error("Error acceptFriendRequest", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.NOT_FOUND
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
    @Operation(summary = "Reject friend request")
    @PutMapping("/rejectRequest/{requestId}")
    public ResponseEntity<Object> rejectFriendRequest(@PathVariable String requestId) {
        try {
            int id = Integer.parseInt(requestId);
            if(id<=0) {
                return APIResponse.responseBuilder(
                        null,
                        "Request id must be greater than 0",
                        HttpStatus.BAD_REQUEST
                );
            }
            friendShipService.rejectFriendRequest(id);
            return APIResponse.responseBuilder(null, "Friend request rejected successfully", HttpStatus.OK);
        } catch (NumberFormatException e) {
            return APIResponse.responseBuilder(
                    null,
                    "Invalid requestId. It must be an integer.",
                    HttpStatus.BAD_REQUEST
            );
        } catch (EntityNotFoundException e) {
            log.error("Error rejectFriendRequest", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.NOT_FOUND
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
    @Operation(summary = "Unfriend")
    @DeleteMapping("/unfriend/{friendId}")
    public ResponseEntity<Object> unfriend(@PathVariable String friendId) {
        try {
            int id = Integer.parseInt(friendId);
            if(id<=0) {
                return APIResponse.responseBuilder(
                        null,
                        "Friend id must be greater than 0",
                        HttpStatus.BAD_REQUEST
                );            }
            friendShipService.unfriend(id);
            return APIResponse.responseBuilder(null, "Unfriended successfully", HttpStatus.OK);
        } catch (NumberFormatException e) {
            return APIResponse.responseBuilder(
                    null,
                    "Invalid friendId. It must be an integer.",
                    HttpStatus.BAD_REQUEST
            );
        } catch (EntityNotFoundException e) {
            log.error("Error unfriend", e);
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        } catch (RuntimeException e) {
            log.error("Error unfriend", e);
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }catch (Exception e) {
            log.error("Error unfriend", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
