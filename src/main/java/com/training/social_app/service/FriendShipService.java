package com.training.social_app.service;

import com.training.social_app.dto.response.FriendShipResponse;
import com.training.social_app.entity.User;

import java.util.List;

public interface FriendShipService {
    List<User> getFriends();
    FriendShipResponse getFriendship(Integer friendId);
    //Get users who received friend requests from the current user
    List<User> getFriendRequests();
    //Get users who sent friend requests to the current user
    List<User> getFriendRequestsToUser();
    FriendShipResponse sendFriendRequest(Integer requesteeId);
    FriendShipResponse acceptFriendRequest(Integer requestId);
    FriendShipResponse rejectFriendRequest(Integer requestId);
    void unfriend(Integer friendId);
}
