package com.training.social_app.service;

import com.training.social_app.dto.request.UserRequest;
import com.training.social_app.entity.User;

import java.util.Optional;

public interface UserService {
    User registerUser(UserRequest request);
    User findByEmail(String email);
    User findByUsername(String username);
    String verifyOtp(String email, String otp);
    String generateForgotPasswordToken(String email);
}
