package com.training.social_app.service.impl;

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
    private UserRepository userRepository;

//    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder; //bcrypt password encoder

    @Override
    public User registerUser(UserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setOtp(generateOtp());
        user.setIsVerified(false);
        return userRepository.save(user);
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
    public String verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found for email: " + email));
        if(user.getOtp() == null || user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            throw new RuntimeException("OTP has expired. Please generate a new OTP");
        }
        if (!user.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }
        user.setIsVerified(true);
        //Reset OTP
        user.setOtp(null);
        user.setOtpExpiry(null);
        return "OTP verified successfully";
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
}