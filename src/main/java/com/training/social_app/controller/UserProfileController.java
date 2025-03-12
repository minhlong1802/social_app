package com.training.social_app.controller;

import com.training.social_app.dto.request.UserProfileRequestDto;
import com.training.social_app.dto.response.APIResponse;
import com.training.social_app.entity.UserProfile;
import com.training.social_app.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/user-profile")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {
    private final UserProfileService userProfileService;

    //Get user profile
    @GetMapping
    public ResponseEntity<Object> getUserProfile() {
        try {
            UserProfile userProfile = userProfileService.getUserProfileByUserId();
            return APIResponse.responseBuilder(userProfile, "User profile retrieved successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error getUserProfile", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error getUserProfile", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    //Update user profile
    @PostMapping
    public ResponseEntity<Object> updateUserProfile(@RequestBody @Valid UserProfileRequestDto userProfile, BindingResult bindingResult) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (bindingResult.hasErrors()) {
                bindingResult.getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
            }
            if (!errors.isEmpty()) {
                return APIResponse.responseBuilder(
                        errors,
                        "Validation failed",
                        HttpStatus.BAD_REQUEST
                );
            }
            UserProfile updatedUserProfile = userProfileService.saveOrUpdateUserProfile(userProfile);
            return APIResponse.responseBuilder(updatedUserProfile, "User profile updated successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error updateUserProfile", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error updateUserProfile", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
