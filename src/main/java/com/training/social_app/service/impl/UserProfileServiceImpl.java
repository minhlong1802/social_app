package com.training.social_app.service.impl;

import com.training.social_app.dto.request.UserProfileRequest;
import com.training.social_app.entity.UserProfile;
import com.training.social_app.entity.User;
import com.training.social_app.repository.UserProfileRepository;
import com.training.social_app.repository.UserRepository;
import com.training.social_app.service.UserProfileService;
import com.training.social_app.utils.DateUtils;
import com.training.social_app.utils.UserContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {
    @Autowired
    private final UserProfileRepository userProfileRepository;

    @Autowired
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Integer getCurrentUserId() {
        User currentUser = userRepository.findById(UserContext.getUser().getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
        return currentUser.getId();
    }

    public UserProfile getUserProfileByUserId() {
        Integer userId = getCurrentUserId();
        return userProfileRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("User profile not found for user id: " + userId));
    }

    @Override
    public UserProfile saveOrUpdateUserProfile(UserProfileRequest userProfile, MultipartFile file) {
        if (userProfile == null) {
            userProfile = new UserProfileRequest();
        }

        // Validate birthdate input
        if (userProfile.getBirthDate() != null && !DateUtils.isValidDate(userProfile.getBirthDate())) {
            throw new RuntimeException("Invalid birth date");
        }

        Integer userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));
        Optional<UserProfile> existingUserProfile = userProfileRepository.findByUserId(userId);
        UserProfile profile;

        // If user profile exists, update it, else create a new one
        if (existingUserProfile.isPresent()) {
            profile = existingUserProfile.get();
        } else {
            profile = new UserProfile();
            profile.setUser(user);
        }

        profile.setFullName(userProfile.getFullName());
        profile.setBirthDate(userProfile.getBirthDate() != null ? LocalDate.parse(userProfile.getBirthDate()) : null);
        profile.setOccupation(userProfile.getOccupation());
        profile.setLocation(userProfile.getLocation());
        profile.setUpdatedAt(LocalDateTime.now());

        if (file != null && !file.isEmpty()) {
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Only image files are allowed");
            }
            try {
                String fileName = userId + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                profile.setAvatarUrl(filePath.toString());
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file", e);
            }
        }

        return userProfileRepository.save(profile);
    }

    @Override
    public UserProfile getUserProfileByProfileId(Integer profileId) {
        return userProfileRepository.findById(profileId).orElseThrow(() -> new EntityNotFoundException("User profile not found for id: " + profileId));
    }

    @Override
    public void deleteUserProfile() {
        Integer userId = getCurrentUserId();
        userProfileRepository.deleteByUserId(userId);
    }

    @Override
    public UserProfile getUserProfileById(Integer id) {
        userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found for id: " + id));
        return userProfileRepository.findByUserId(id).orElseThrow(() -> new EntityNotFoundException("User profile not found for id: " + id));
    }
}
