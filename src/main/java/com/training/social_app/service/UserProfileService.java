package com.training.social_app.service;

import com.training.social_app.dto.request.UserProfileRequest;
import com.training.social_app.entity.UserProfile;

public interface UserProfileService {
    UserProfile getUserProfileByUserId();

    UserProfile saveOrUpdateUserProfile(UserProfileRequest userProfile);
}
