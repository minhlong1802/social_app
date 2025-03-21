package com.training.social_app.repository;

import com.training.social_app.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByPostId(Integer postId);

    //count comments for a given user in the past week
    @Query("SELECT COUNT(c) FROM Comment c where c.user.id = :userId and c.createdAt between :startDate and :endDate")
    int countCommentsByUserIdAndCreatedAtBetween(Integer userId, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

    //count comments for a post
    @Query("SELECT COUNT(c) FROM Comment c where c.post.id = :postId")
    int countCommentsByPostId(Integer postId);
}
