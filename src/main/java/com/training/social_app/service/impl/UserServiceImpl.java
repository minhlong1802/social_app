package com.training.social_app.service.impl;

import com.training.social_app.dto.request.DeleteRequest;
import com.training.social_app.dto.request.LoginRequest;
import com.training.social_app.dto.request.UserRequest;
import com.training.social_app.dto.response.UserResponse;
import com.training.social_app.entity.User;
import com.training.social_app.enums.Role;
import com.training.social_app.exception.UserForbiddenException;
import com.training.social_app.repository.UserRepository;
import com.training.social_app.service.UserService;
import com.training.social_app.utils.UserContext;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder; //bcrypt password encoder

    @Override
    public User registerUser(UserRequest request) {
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
        userRepository.save(user);

        return user;
    }

    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found for username: " + username));
    }

    @Override
    public String login(LoginRequest loginRequest) {
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
            throw new RuntimeException("Verified failed. OTP is invalid or expired");
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
        if (newPassword.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long");
        }
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        user.setForgotPasswordToken(null);
        user.setForgotPasswordTokenExpiry(null);
        userRepository.save(user);
    }

    @Override
    public void deleteUser() {
        Integer userId = UserContext.getUser().getUser().getId();
        userRepository.deleteById(userId);
    }

    @Override
    public void deleteUsers(DeleteRequest request) {
        User user = userRepository.findById(UserContext.getUser().getUser().getId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new UserForbiddenException("User is not allowed to delete users");
        }

        List<Integer> ids = request.getIds();
        List<User> usersToDelete = userRepository.findAllById(ids);

        List<Integer> existingIds = usersToDelete.stream()
                .map(User::getId)
                .toList();

        List<Integer> notFoundIds = ids.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();

        if (!notFoundIds.isEmpty()) {
            throw new EntityNotFoundException("Users not found for ids: " + notFoundIds);
        }

        userRepository.deleteAll(usersToDelete);
    }

    @Override
    public List<UserResponse> findAll(String searchText, int page, int size) {
        User user = userRepository.findById(UserContext.getUser().getUser().getId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new UserForbiddenException("User is not allowed to see all users");
        }
            if (page > 0) {
                page = page - 1;
            }
            Pageable pageable = PageRequest.of(page, size);
            Specification<User> specification = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (searchText != null && !searchText.isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("username"), "%" + searchText + "%"));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };

            Page<User> pageUser = userRepository.findAll(specification, pageable);
            return pageUser.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
    }

    private UserResponse convertToDTO(User user) {
        UserResponse userDTO = new UserResponse();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    @Override
    public UserResponse findById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));
        return convertToDTO(user);
    }
}