package com.training.social_app.repository;

import com.training.social_app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByForgotPasswordToken(String token);
    @Query("SELECT u FROM User u JOIN u.userProfile up WHERE up.fullName LIKE %:fullName%")
    Page<User> findByUserProfileFullNameContaining(@Param("fullName") String fullName, Pageable pageable);
}
