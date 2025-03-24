package com.training.social_app.repository;

import com.training.social_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    User findUserByEmailAndPassword(String email, String password);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByOtpAndEmail(String otp, String email);
    Optional<User> findByForgotPasswordToken(String token);
    List<User> findAllByUsernameContaining(String name);
}
