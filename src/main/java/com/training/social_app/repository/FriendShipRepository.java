package com.training.social_app.repository;

import com.training.social_app.entity.FriendShip;
import com.training.social_app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip, Integer>, JpaSpecificationExecutor<FriendShip> {
    //Find a friendship by user who sent the request and the user who received the request
    Optional<FriendShip> findByUser1IdAndUser2Id(Integer requesterId, Integer user2Id);

    //Find all friends of a user
    @Query("""
    SELECT u FROM User u\s
    WHERE u.id IN (
        SELECT CASE\s
            WHEN f.user1.id = :userId THEN f.user2.id
            ELSE f.user1.id\s
        END\s
        FROM FriendShip f
        WHERE (f.user1.id = :userId OR f.user2.id = :userId)\s
        AND f.status = 'ACCEPTED'
    )
""")    List<User> getFriendsByUserId(Integer userId);

    @Query("""
    SELECT u FROM User u\s
    WHERE u.id IN (
        SELECT CASE\s
            WHEN f.user1.id = :userId THEN f.user2.id
            ELSE f.user1.id\s
        END\s
        FROM FriendShip f
        WHERE (f.user1.id = :userId OR f.user2.id = :userId)\s
        AND f.status = 'ACCEPTED'
    )
""")
    Page<User> findFriendsByUserId(Integer userId, Pageable pageable);


    @Query("SELECT f FROM FriendShip f WHERE f.user1.id = :userId AND f.status = 'PENDING'")
    Page<FriendShip> findFriendRequestsByUserId(Integer userId, Pageable pageable);

    @Query("SELECT f FROM FriendShip f WHERE f.user2.id = :userId AND f.status = 'PENDING'")
    Page<FriendShip> findFriendRequestsToUserId(Integer userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE FriendShip f SET f.status = 'ACCEPTED' WHERE f.user2.id = :userId AND f.id = :requestId AND f.status = 'PENDING'")
    void acceptFriendRequest(Integer userId, Integer requestId);

    //Reject a friend request to current user by id and status
    @Modifying
    @Transactional
    @Query("UPDATE FriendShip f SET f.status = 'REJECTED' where f.user2.id = :userId and f.id = :requestId and f.status = 'PENDING'")
    void rejectFriendRequest(Integer userId, Integer requestId);

    //Count new friends of a user in the past week
    @Query("SELECT COUNT(f) FROM FriendShip f where f.user2.id = :userId and f.status = 'ACCEPTED' and f.createdAt between :startDate and :endDate")
    int countNewFriendsByUserIdInPastWeek(Integer userId, LocalDateTime startDate, LocalDateTime endDate);
}
