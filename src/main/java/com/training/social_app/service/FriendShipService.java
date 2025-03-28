package com.training.social_app.service;

import com.training.social_app.dto.response.FriendShipRequestResponse;
import com.training.social_app.dto.response.FriendShipResponse;
import com.training.social_app.dto.response.UserResponse;

import java.util.List;

public interface FriendShipService {
    List<UserResponse> getFriends(int page, int size);
    FriendShipResponse getFriendship(Integer friendId);
    //Get users who received friend requests from the current user
    List<FriendShipRequestResponse> getFriendRequests(int page, int size);
    //Get users who sent friend requests to the current user
    List<FriendShipRequestResponse> getFriendRequestsToUser(int page, int size);
    FriendShipResponse sendFriendRequest(Integer requesteeId);
    FriendShipResponse acceptFriendRequest(Integer requestId);
    FriendShipResponse rejectFriendRequest(Integer requestId);
    void unfriend(Integer friendId);
}
