package com.training.social_app.service;

import com.training.social_app.dto.request.DeleteRequest;
import com.training.social_app.dto.request.LoginRequest;
import com.training.social_app.dto.request.UserRequest;
import com.training.social_app.dto.response.DetailUserResponse;
import com.training.social_app.entity.User;

import java.util.Map;

public interface UserService {
    String login(LoginRequest request);
    User registerUser(UserRequest request);
    User findByUsername(String username);
    boolean verifyOtp(User user, String otp);
    String generateForgotPasswordToken(String email);
    void resetPassword(String token, String newPassword);
    void deleteUser();
    void deleteUsers(DeleteRequest request);
    Map<String,Object> findAll(String searchText, int page, int size);
    DetailUserResponse findById(Integer userId);
    DetailUserResponse getUserProfile();
    //search user by full name (for normal user)
    Map<String,Object> searchUser(String searchText, int page, int size);
}
