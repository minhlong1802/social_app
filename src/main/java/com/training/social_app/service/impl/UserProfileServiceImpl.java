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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {
    @Autowired
    private final UserProfileRepository userProfileRepository;

    @Autowired
    private final UserRepository userRepository;

    private Integer getCurrentUserId() {
        User currentUser = userRepository.findById(UserContext.getUser().getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
        return currentUser.getId();
    }

    public UserProfile getUserProfileByUserId() {
        Integer userId = getCurrentUserId();
        return userProfileRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("User profile not found for user id: " + userId));
    }

    public UserProfile saveOrUpdateUserProfile(UserProfileRequest userProfile) {
        //validation for birthdate input
        if(!DateUtils.isValidDate(userProfile.getBirthDate())){
            throw new RuntimeException("Invalid birth date");
        }
        Integer userId = getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found for id: " + userId));
        Optional<UserProfile> existingUserProfile = userProfileRepository.findByUserId(userId);
        UserProfile profile;
        if(existingUserProfile.isPresent()){
            profile = existingUserProfile.get();
            profile.setFullName(userProfile.getFullName());
            profile.setBirthDate(LocalDate.parse(userProfile.getBirthDate()));
            profile.setOccupation(userProfile.getOccupation());
            profile.setLocation(userProfile.getLocation());
            profile.setAvatarUrl(userProfile.getAvatarUrl());
        } else{
            profile = new UserProfile();
            profile.setUser(user);
            profile.setFullName(userProfile.getFullName());
            profile.setBirthDate(LocalDate.parse(userProfile.getBirthDate()));
            profile.setOccupation(userProfile.getOccupation());
            profile.setLocation(userProfile.getLocation());
            profile.setAvatarUrl(userProfile.getAvatarUrl());
        }
        return userProfileRepository.save(profile);
    }
}
