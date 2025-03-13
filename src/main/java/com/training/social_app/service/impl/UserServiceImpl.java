package com.training.social_app.service.impl;

import com.training.social_app.dto.request.LoginRequest;
import com.training.social_app.dto.request.UserRequest;
import com.training.social_app.entity.User;
import com.training.social_app.enums.Role;
import com.training.social_app.repository.UserRepository;
import com.training.social_app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder; //bcrypt password encoder

    @Override
    public String registerUser(UserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }
        //Create username from email
        String username = request.getEmail().split("@")[0];

        User user = new User();
        user.setUsername(username);
        user.setEmail(request.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setIsVerified(false);
        userRepository.save(user);

        return "User registered successfully. Your username is " + username;
    }

    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found for email: " + email));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found for username: " + username));
    }

    @Override
    public String login(LoginRequest loginRequest){
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(() -> new RuntimeException("User not found for username: " + loginRequest.getUsername()));
        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);
        return "Your OTP is " + otp;
    }

    public boolean verifyOtp(User user, String otp) {
        if (user.getOtp() == null || user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            throw new RuntimeException("OTP expired");
        }
        if (!user.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public String generateForgotPasswordToken(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found for email: " + email));
        String token = UUID.randomUUID().toString();
        user.setForgotPasswordToken(token);
        user.setForgotPasswordTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);
        return token;
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByForgotPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (LocalDateTime.now().isAfter(user.getForgotPasswordTokenExpiry())) {
            throw new RuntimeException("Invalid or expired token");
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        user.setForgotPasswordToken(null);
        user.setForgotPasswordTokenExpiry(null);
        userRepository.save(user);
    }
}