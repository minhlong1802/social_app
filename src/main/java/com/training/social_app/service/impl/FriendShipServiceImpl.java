package com.training.social_app.service.impl;

import com.training.social_app.entity.FriendShip;
import com.training.social_app.entity.Like;
import com.training.social_app.entity.User;
import com.training.social_app.enums.RequestStatus;
import com.training.social_app.repository.FriendShipRepository;
import com.training.social_app.repository.PostRepository;
import com.training.social_app.repository.UserRepository;
import com.training.social_app.service.FriendShipService;
import com.training.social_app.utils.UserContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

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

    @Override
    public List<User> getFriends() {
        Integer userId = getCurrentUserId();
        return friendShipRepository.findFriendsByUserId(userId);
    }

    @Override
    public FriendShip getFriendship(Integer friendId) {
        Integer userId = getCurrentUserId();
        return friendShipRepository.findByUser1IdAndUser2Id(userId, friendId)
                .orElseThrow(() -> new RuntimeException("Friendship not found for user id: " + userId + " and friend id: " + friendId));
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
        FriendShip friendShip = new FriendShip();
        friendShip.setUser1(userRepository.findById(requesterId).orElseThrow(() -> new RuntimeException("User not found for requester id: " + requesterId)));
        friendShip.setUser2(userRepository.findById(requesteeId).orElseThrow(() -> new RuntimeException("User not found for requestee id: " + requesteeId)));
        friendShip.setStatus(RequestStatus.PENDING);
        friendShipRepository.save(friendShip);
    }

    @Override
    public void acceptFriendRequest(Integer requestId) {
        Integer userId = getCurrentUserId();
        if (friendShipRepository.findById(requestId).isEmpty()) {
            throw new RuntimeException("Friend request not found for id: " + requestId);
        }
        friendShipRepository.acceptFriendRequest(userId, requestId);
    }

    @Override
    public void rejectFriendRequest(Integer requestId) {
        Integer userId = getCurrentUserId();
        if (friendShipRepository.findById(requestId).isEmpty()) {
            throw new RuntimeException("Friend request not found for id: " + requestId);
        }
        friendShipRepository.rejectFriendRequest(userId, requestId);
    }

    @Override
    public void unfriend(Integer friendId) {
        Integer userId = getCurrentUserId();
        FriendShip friendShip = friendShipRepository.findByUser1IdAndUser2Id(userId, friendId)
                .orElseThrow(() -> new RuntimeException("Friendship not found for user id: " + userId + " and friend id: " + friendId));
        if(friendShip.getStatus() != RequestStatus.ACCEPTED) {
            throw new RuntimeException("Friend request not accepted");
        }
        friendShipRepository.delete(friendShip);
    }

    @Override
    public int countFriendsInPastWeek() {
        Integer userId = getCurrentUserId();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getWeeksInWeekYear());
        if(cal.getFirstDayOfWeek() != Calendar.MONDAY){
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        LocalDate startDate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = LocalDateTime.now();
        return friendShipRepository.countNewFriendsByUserIdInPastWeek(userId, startDateTime, endDateTime);
    }
}
