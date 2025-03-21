package com.training.social_app.repository;

import com.training.social_app.entity.FriendShip;
import com.training.social_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip, Integer> {
    //Find a friendship by user who sent the request and the user who received the request
    Optional<FriendShip> findByUser1IdAndUser2Id(Integer requesterId, Integer requesteeId);

    //Find all friends of a user
    @Query("SELECT f.user2 FROM FriendShip f where f.user1.id = :userId and f.status = 'ACCEPTED'")
    List<User> findFriendsByUserId(Integer userId);

    //Find all friend requests sent by a user
    @Query("SELECT f.user2 FROM FriendShip f where f.user1.id = :userId and f.status = 'PENDING'")
    List<User> findFriendRequestsByUserId(Integer userId);

    //Find all friend requests received by a user
    @Query("SELECT f.user1 FROM FriendShip f where f.user2.id = :userId and f.status = 'PENDING'")
    List<User> findFriendRequestsToUserId(Integer userId);

    //Accept a friend request to current user by id and status
    @Modifying
    @Transactional
    @Query("UPDATE FriendShip f SET f.status = 'ACCEPTED' WHERE f.user2.id = :userId AND f.id = :requestId AND f.status = 'PENDING'")
    void acceptFriendRequest(Integer userId, Integer requestId);

    //Reject a friend request to current user by id and status
    @Modifying
    @Transactional
    @Query("UPDATE FriendShip f SET f.status = 'REJECTED' where f.user2.id = :userId and f.id = :requestId and f.status = 'PENDING'")
    void rejectFriendRequest(Integer userId, Integer requestId);

    //Count friends of a user
    @Query("SELECT COUNT(f) FROM FriendShip f where f.user1.id = :userId and f.status = 'ACCEPTED'")
    int countFriendsByUserId(Integer userId);

    //Count friend requests sent by a user
    @Query("SELECT COUNT(f) FROM FriendShip f where f.user1.id = :userId and f.status = 'PENDING'")
    int countFriendRequestsByUserId(Integer userId);

    //Count friend requests received by a user
    @Query("SELECT COUNT(f) FROM FriendShip f where f.user2.id = :userId and f.status = 'PENDING'")
    int countFriendRequestsToUserId(Integer userId);

    //Count new friends of a user in the past week
    @Query("SELECT COUNT(f) FROM FriendShip f where f.user2.id = :userId and f.status = 'ACCEPTED' and f.createdAt between :startDate and :endDate")
    int countNewFriendsByUserIdInPastWeek(Integer userId, LocalDateTime startDate, LocalDateTime endDate);
}
