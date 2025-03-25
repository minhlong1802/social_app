package com.training.social_app.service.impl;

import com.training.social_app.entity.FriendShip;
import com.training.social_app.entity.User;
import com.training.social_app.enums.RequestStatus;
import com.training.social_app.repository.FriendShipRepository;
import com.training.social_app.repository.UserRepository;
import com.training.social_app.service.FriendShipService;
import com.training.social_app.utils.UserContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FriendShipServiceImpl implements FriendShipService {
    private final FriendShipRepository friendShipRepository;

    @Autowired
    private final UserRepository userRepository;

    private Integer getCurrentUserId() {
        User currentUser = userRepository.findById(UserContext.getUser().getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
        return currentUser.getId();
    }

    // Get friends of user
    @Override
    public List<User> getFriends() {
        Integer userId = getCurrentUserId();
        return friendShipRepository.findFriendsByUserId(userId);
    }

    @Override
    public FriendShip getFriendship(Integer friendId) {
        Integer userId = getCurrentUserId();
        return friendShipRepository.findByUser1IdAndUser2Id(userId, friendId)
                .orElseThrow(() -> new EntityNotFoundException("Friendship not found for user id: " + userId + " and friend id: " + friendId));
    }

    @Override
    public List<User> getFriendRequests() {
        Integer userId = getCurrentUserId();
        return friendShipRepository.findFriendRequestsByUserId(userId);
    }

    @Override
    public List<User> getFriendRequestsToUser() {
        Integer userId = getCurrentUserId();
        return friendShipRepository.findFriendRequestsToUserId(userId);
    }

    @Override
    public void sendFriendRequest(Integer requesteeId) {
        Integer requesterId = getCurrentUserId();
        if(friendShipRepository.findByUser1IdAndUser2Id(userRepository.findById(requesteeId).orElseThrow(() -> new EntityNotFoundException("User not found for requestee id: " + requesteeId)).getId(), requesterId).isPresent()) {
            throw new RuntimeException("Friend request already sent or accepted");
        }
        FriendShip friendShip = new FriendShip();
        friendShip.setUser1(userRepository.findById(requesterId).orElseThrow(() -> new EntityNotFoundException("User not found for requester id: " + requesterId)));
        friendShip.setUser2(userRepository.findById(requesteeId).orElseThrow(() -> new EntityNotFoundException("User not found for requestee id: " + requesteeId)));
        friendShip.setStatus(RequestStatus.PENDING);
        friendShipRepository.save(friendShip);
    }

    @Override
    public void acceptFriendRequest(Integer requestId) {
        Integer userId = getCurrentUserId();
        if (friendShipRepository.findById(requestId).isEmpty()) {
            throw new EntityNotFoundException("Friend request not found for id: " + requestId);
        }
        if(!Objects.equals(friendShipRepository.findById(requestId).get().getUser2().getId(), userId)) {
            throw new EntityNotFoundException("Friend request not found for user id: " + userId);
        }
        friendShipRepository.acceptFriendRequest(userId, requestId);
    }

    @Override
    public void rejectFriendRequest(Integer requestId) {
        Integer userId = getCurrentUserId();
        if (friendShipRepository.findById(requestId).isEmpty()) {
            throw new EntityNotFoundException("Friend request not found for id: " + requestId);
        }
        if(!Objects.equals(friendShipRepository.findById(requestId).get().getUser2().getId(), userId)) {
            throw new EntityNotFoundException("Friend request not found for user id: " + userId);
        }
        friendShipRepository.rejectFriendRequest(userId, requestId);
    }

    @Override
    public void unfriend(Integer friendId) {
        Integer userId = getCurrentUserId();
        if(Objects.equals(userId, friendId)) {
            throw new RuntimeException("User cannot unfriend themselves");
        }
        FriendShip friendShip = friendShipRepository.findByUser1IdAndUser2Id(userId, friendId)
                .orElseThrow(() -> new EntityNotFoundException("Friendship not found for user id: " + userId + " and friend id: " + friendId));
        if(friendShip.getStatus() != RequestStatus.ACCEPTED) {
            throw new RuntimeException("Friend request not accepted");
        }
        friendShipRepository.delete(friendShip);
    }
}
