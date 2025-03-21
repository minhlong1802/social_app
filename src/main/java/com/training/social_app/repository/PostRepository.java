package com.training.social_app.repository;

import com.training.social_app.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAllByUserId(Integer userId);
    @Query("SELECT COUNT(p) FROM Post p where p.user.id = :userId and p.createdAt between :startDate and :endDate")
    int countByUserIdAndCreatedAtBetween(Integer userId, LocalDateTime startDate, LocalDateTime endDate);
    List<Post> findByUserIdIn(List<Integer> userIds, Sort sort);
}
