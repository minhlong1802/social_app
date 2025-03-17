package com.training.social_app.repository;

import com.training.social_app.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAllByUserId(Integer userId);
    int countByUserIdAndCreatedAtBetween(Integer userId, LocalDate startDate, LocalDate endDate);
    List<Post> findByUserIdIn(List<Integer> userIds, Sort sort);
}
