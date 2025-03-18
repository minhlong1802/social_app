package com.training.social_app.service;

import com.training.social_app.entity.FriendShip;
import com.training.social_app.entity.User;

import java.util.List;

public interface FriendShipService {
    List<User> getFriends();
    FriendShip getFriendship(Integer friendId);
    //Get users who received friend requests from the current user
    List<User> getFriendRequests();
    //Get users who sent friend requests to the current user
    List<User> getFriendRequestsToUser();
    void sendFriendRequest(Integer requesteeId);
    void acceptFriendRequest(Integer requestId);
    void rejectFriendRequest(Integer requestId);
    void unfriend(Integer friendId);
    int countFriendsInPastWeek();
}
