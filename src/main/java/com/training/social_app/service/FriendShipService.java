package com.training.social_app.service;

import com.training.social_app.dto.response.FriendShipResponse;

import java.util.Map;

public interface FriendShipService {
    Map<String, Object> getFriends(int page, int size);
    FriendShipResponse getFriendship(Integer friendId);
    //Get users who received friend requests from the current user
    Map<String, Object> getFriendRequests(int page, int size);
    //Get users who sent friend requests to the current user
    Map<String, Object> getFriendRequestsToUser(int page, int size);
    FriendShipResponse sendFriendRequest(Integer requesteeId);
    FriendShipResponse acceptFriendRequest(Integer requestId);
    void rejectFriendRequest(Integer requestId);
    void unfriend(Integer friendId);
}
