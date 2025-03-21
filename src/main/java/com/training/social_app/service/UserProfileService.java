package com.training.social_app.service;

import com.training.social_app.dto.request.UserProfileRequest;
import com.training.social_app.entity.UserProfile;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {
    UserProfile getUserProfileByUserId();
    UserProfile saveOrUpdateUserProfile(UserProfileRequest userProfile,MultipartFile file);
    UserProfile getUserProfileByProfileId(Integer profileId);
}
