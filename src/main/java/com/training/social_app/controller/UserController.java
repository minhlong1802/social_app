package com.training.social_app.controller;

import com.training.social_app.dto.request.DeleteRequest;
import com.training.social_app.dto.response.APIResponse;
import com.training.social_app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteUser() {
        try {
            userService.deleteUser();
            return APIResponse.responseBuilder(
                    null,
                    "User deleted successfully",
                    HttpStatus.OK
            );
        }  catch (Exception e) {
            log.error("Error deleteUser", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUsers(@RequestBody DeleteRequest request) {
        try {
            if (request.getIds() == null || request.getIds().isEmpty()) {
                return APIResponse.responseBuilder(null, "The data sent is not in the correct format.", HttpStatus.BAD_REQUEST);
            }
            userService.deleteUsers(request);
            return APIResponse.responseBuilder(
                    null,
                    "User deleted successfully",
                    HttpStatus.OK
            );
        } catch (EntityNotFoundException e) {
            log.error("Error deleteUser", e);
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        } catch (RuntimeException e) {
            log.error("Error deleteUser", e);
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.UNAUTHORIZED
            );
        } catch (Exception e) {
            log.error("Error deleteUser", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            return APIResponse.responseBuilder(
                    userService.findAll(),
                    "Users retrieved successfully",
                    HttpStatus.OK
            );
        } catch (RuntimeException e) {
            log.error("Error findAll", e);
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.UNAUTHORIZED
            );
        } catch (Exception e) {
            log.error("Error findAll", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
