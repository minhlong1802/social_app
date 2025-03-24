package com.training.social_app.repository;

import com.training.social_app.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    //Find a user profile by user id
    Optional<UserProfile> findByUserId(Integer userId);

    void deleteByUserId(Integer userId);
}
