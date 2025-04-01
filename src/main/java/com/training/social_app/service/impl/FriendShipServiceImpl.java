package com.training.social_app.service.impl;

import com.training.social_app.dto.response.FriendShipRequestResponse;
import com.training.social_app.dto.response.FriendShipResponse;
import com.training.social_app.dto.response.UserResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    private UserResponse convertToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setFullName(user.getUserProfile().getFullName());
        userResponse.setAvatarUrl(user.getUserProfile().getAvatarUrl());
        return userResponse;
    }

    // Get friends of user
    @Override
    public Map<String, Object> getFriends(int page, int size) {
        Integer userId = getCurrentUserId();
        if (page > 0) {
            page = page - 1;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<User> friendsPage = friendShipRepository.findFriendsByUserId(userId, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("listFriends", friendsPage.getContent().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList()));
        response.put("pageSize", friendsPage.getSize());
        response.put("pageNo", friendsPage.getNumber() + 1);
        response.put("totalPage", friendsPage.getTotalPages());
        return response;
    }

    @Override
    public FriendShipResponse getFriendship(Integer friendId) {
        Integer userId = getCurrentUserId();
        FriendShip friendShip = friendShipRepository.findByUser1IdAndUser2Id(userId, friendId)
                .orElseThrow(() -> new EntityNotFoundException("Friendship not found for user id: " + userId + " and friend id: " + friendId));
        return convertToDto(friendShip);
    }

    private FriendShipRequestResponse convertToFriendShipRequestResponse(FriendShip friendShip, boolean isRequester) {
        FriendShipRequestResponse friendShipRequestResponse = new FriendShipRequestResponse();
        friendShipRequestResponse.setId(friendShip.getId());
        User user = isRequester ? friendShip.getUser2() : friendShip.getUser1();
        if (user.getUserProfile() != null) {
            friendShipRequestResponse.setUserId(user.getId());
            friendShipRequestResponse.setUserFullName(user.getUserProfile().getFullName());
            friendShipRequestResponse.setUserAvatarUrl(user.getUserProfile().getAvatarUrl());
        } else {
            friendShipRequestResponse.setUserId(user.getId());
            friendShipRequestResponse.setUserFullName(null);
            friendShipRequestResponse.setUserAvatarUrl(null);
        }
        friendShipRequestResponse.setStatus(friendShip.getStatus());
        friendShipRequestResponse.setCreatedAt(friendShip.getCreatedAt());
        return friendShipRequestResponse;
    }

    @Override
    public Map<String, Object> getFriendRequests(int page, int size) {
        Integer userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<FriendShip> friendRequestsPage = friendShipRepository.findFriendRequestsByUserId(userId, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("listFriendRequests", friendRequestsPage.getContent().stream()
                .map(friendShip -> convertToFriendShipRequestResponse(friendShip, true))
                .collect(Collectors.toList()));
        response.put("pageSize", friendRequestsPage.getSize());
        response.put("pageNo", friendRequestsPage.getNumber() + 1);
        response.put("totalPage", friendRequestsPage.getTotalPages());
        return response;
    }

    @Override
    public Map<String, Object> getFriendRequestsToUser(int page, int size) {
        Integer userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<FriendShip> friendRequestsToUserPage = friendShipRepository.findFriendRequestsToUserId(userId, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("listFriendRequests", friendRequestsToUserPage.getContent().stream()
                .map(friendShip -> convertToFriendShipRequestResponse(friendShip, false))
                .collect(Collectors.toList()));
        response.put("pageSize", friendRequestsToUserPage.getSize());
        response.put("pageNo", friendRequestsToUserPage.getNumber() + 1);
        response.put("totalPage", friendRequestsToUserPage.getTotalPages());
        return response;
    }

    @Override
    public FriendShipResponse sendFriendRequest(Integer requesteeId) {
        Integer requesterId = getCurrentUserId();
        if(friendShipRepository.findByUser1IdAndUser2Id(requesterId,userRepository.findById(requesteeId).orElseThrow(() -> new EntityNotFoundException("User not found for requestee id: " + requesteeId)).getId() ).isPresent()) {
            throw new RuntimeException("Friend request already sent or accepted");
        }
        if(friendShipRepository.findByUser1IdAndUser2Id(requesteeId,requesterId).isPresent()) {
            throw new RuntimeException("Friend request already sent or accepted");
        }
        if(Objects.equals(requesterId, requesteeId)) {
            throw new RuntimeException("User cannot send friend request to themselves");
        }
        FriendShip friendShip = new FriendShip();
        friendShip.setUser1(userRepository.findById(requesterId).orElseThrow(() -> new EntityNotFoundException("User not found for requester id: " + requesterId)));
        friendShip.setUser2(userRepository.findById(requesteeId).orElseThrow(() -> new EntityNotFoundException("User not found for requestee id: " + requesteeId)));
        friendShip.setStatus(RequestStatus.PENDING);
        return convertToDto( friendShipRepository.save(friendShip));
    }

    private FriendShipResponse convertToDto(FriendShip friendShip) {
        FriendShipResponse friendShipResponse = new FriendShipResponse();
        friendShipResponse.setId(friendShip.getId());
        friendShipResponse.setUser1Id(friendShip.getUser1().getId());
        friendShipResponse.setUser2Id(friendShip.getUser2().getId());
        friendShipResponse.setStatus(friendShip.getStatus());
        friendShipResponse.setCreatedAt(friendShip.getCreatedAt());
        friendShipResponse.setUpdatedAt(friendShip.getUpdatedAt())   ;
        return friendShipResponse;
    }

    @Override
    public FriendShipResponse acceptFriendRequest(Integer requestId) {
        Integer userId = getCurrentUserId();
        FriendShip friendShip = friendShipRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Friend request not found for id: " + requestId));
        if (!Objects.equals(friendShip.getUser2().getId(), userId)) {
            throw new EntityNotFoundException("Friend request not found for user id: " + userId);
        }
        friendShipRepository.acceptFriendRequest(userId, requestId);
        friendShip.setStatus(RequestStatus.ACCEPTED);
        return convertToDto(friendShip);
    }

    @Override
    public void rejectFriendRequest(Integer requestId) {
        Integer userId = getCurrentUserId();
        FriendShip friendShip = friendShipRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Friend request not found for id: " + requestId));
        if (!Objects.equals(friendShip.getUser2().getId(), userId)) {
            throw new EntityNotFoundException("Friend request not found for user id: " + userId);
        }
        friendShipRepository.deleteById(requestId);
    }

    @Override
    public void unfriend(Integer friendId) {
        Integer userId = getCurrentUserId();
        if (Objects.equals(userId, friendId)) {
            throw new RuntimeException("User cannot unfriend themselves");
        }

        FriendShip friendShip = friendShipRepository.findByUser1IdAndUser2Id(userId, friendId)
                .orElseGet(() -> friendShipRepository.findByUser1IdAndUser2Id(friendId, userId)
                        .orElseThrow(() -> new EntityNotFoundException("Friendship not found for user id: " + userId + " and friend id: " + friendId)));

        if (friendShip.getStatus() != RequestStatus.ACCEPTED) {
            throw new RuntimeException("Friend request not accepted");
        }

        friendShipRepository.delete(friendShip);
    }
}
