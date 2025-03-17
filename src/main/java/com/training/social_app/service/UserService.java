package com.training.social_app.service;

import com.training.social_app.dto.request.LoginRequest;
import com.training.social_app.dto.request.UserRequest;
import com.training.social_app.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.Optional;

public interface UserService {
    String login(LoginRequest request);
    String registerUser(UserRequest request);
    User findByEmail(String email);
    User findByUsername(String username);
    boolean verifyOtp(User user, String otp);
    String generateForgotPasswordToken(String email);
    void resetPassword(String token, String newPassword);
    void deleteUser();
}
