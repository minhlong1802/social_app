package com.training.social_app.repository;

import com.training.social_app.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> , JpaSpecificationExecutor<Like> {
    //Find a like by user who liked and the post that was liked
    Optional<Like> findByUserIdAndPostId(Integer userId, Integer postId);

    //Count likes for a given user's post between two dates
    @Query("SELECT COUNT(l) FROM Like l where l.post.user.id = :userId and l.createdAt between :startDate and :endDate")
    int countLikesByUserAndDate(Integer userId, LocalDate startDate, LocalDate endDate);
}
