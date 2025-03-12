package com.training.social_app.service;

import com.training.social_app.dto.request.UserProfileRequestDto;
import com.training.social_app.entity.UserProfile;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserProfileService {
    UserProfile getUserProfileByUserId();

    UserProfile saveOrUpdateUserProfile(UserProfileRequestDto userProfile);
}
