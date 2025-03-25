package com.training.social_app.controller;

import com.training.social_app.dto.request.DeleteRequest;
import com.training.social_app.dto.response.APIResponse;
import com.training.social_app.exception.UserForbiddenException;
import com.training.social_app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Delete current account")
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

    @Operation(summary = "Delete users")
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
        } catch (UserForbiddenException e) {
            log.error("Error deleteUser", e);
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.FORBIDDEN
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

    @Operation(summary = "Find all users")
    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "") String searchText,
                                     @RequestParam(defaultValue = "1") Integer pageNo,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            return APIResponse.responseBuilder(
                    userService.findAll(searchText, pageNo, pageSize),
                    "Users retrieved successfully",
                    HttpStatus.OK
            );
        }catch (UserForbiddenException e) {
            log.error("Error findAll", e);
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.FORBIDDEN
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

    @Operation(summary = "Find user by id")
    @GetMapping("/{userId}")
    public ResponseEntity<?> findById(@PathVariable Integer userId) {
        try {
            return APIResponse.responseBuilder(
                    userService.findById(userId),
                    "User retrieved successfully",
                    HttpStatus.OK
            );
        } catch (EntityNotFoundException e) {
            log.error("Error findById", e);
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            log.error("Error findById", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
