package com.training.social_app.controller;

import com.training.social_app.dto.request.UserProfileRequest;
import com.training.social_app.dto.response.APIResponse;
import com.training.social_app.entity.UserProfile;
import com.training.social_app.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @Operation(summary = "Get user profile")
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
    @Operation(summary = "Update user profile")
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> saveOrUpdateUserProfile(@RequestPart(name= "userProfile" , required = false)  @Valid UserProfileRequest userProfile,
                                                     @RequestPart(name="avatarUrl", required = false)  MultipartFile file, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return APIResponse.responseBuilder(
                    errors,
                    "Validation failed",
                    HttpStatus.BAD_REQUEST
            );
        }
        try {
            UserProfile updatedUserProfile = userProfileService.saveOrUpdateUserProfile(userProfile, file);
            return APIResponse.responseBuilder(updatedUserProfile, "User profile updated successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error saveOrUpdateUserProfile", e);
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

    //Get user profile by profile id
    @Operation(summary = "Get user profile by profile id")
    @GetMapping("/{profileId}")
    public ResponseEntity<Object> getUserProfileByProfileId(@PathVariable String profileId) {
        try {
            int id = Integer.parseInt(profileId);
            if(id <= 0) {
                return APIResponse.responseBuilder(
                        null,
                        "Profile id must be greater than 0",
                        HttpStatus.BAD_REQUEST
                );
            }
            UserProfile userProfile = userProfileService.getUserProfileByProfileId(id);
            return APIResponse.responseBuilder(userProfile, "User profile retrieved successfully", HttpStatus.OK);
        } catch (NumberFormatException e) {
            return APIResponse.responseBuilder(
                    null,
                    "Invalid profileId. It must be an integer.",
                    HttpStatus.BAD_REQUEST
            );
        } catch (EntityNotFoundException e) {
            log.error("Error getUserProfileByProfileId", e);
            return APIResponse.responseBuilder(
                    null,
                    Objects.requireNonNull(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error getUserProfileByProfileId", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    //Delete user profile
    @Operation(summary = "Delete user profile")
    @DeleteMapping
    public ResponseEntity<Object> deleteUserProfile() {
        try {
            userProfileService.deleteUserProfile();
            return APIResponse.responseBuilder(null, "User profile deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleteUserProfile", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
