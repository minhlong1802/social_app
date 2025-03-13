package com.training.social_app.controller;

import com.training.social_app.dto.request.LoginRequest;
import com.training.social_app.dto.request.UserRequest;
import com.training.social_app.dto.response.APIResponse;
import com.training.social_app.dto.response.UserDto;
import com.training.social_app.entity.User;
import com.training.social_app.service.UserService;
import com.training.social_app.service.impl.UserDetailsServiceImpl;
import com.training.social_app.service.impl.UserServiceImpl;
import com.training.social_app.utils.JwtTokenUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtil;
    private final UserServiceImpl userServiceImpl;
    private final UserDetailsServiceImpl userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //Login
    @RequestMapping(value = "/api/auth/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest authenticationRequest,
                                                       BindingResult bindingResult) {
        // Handle validation errors
        if (bindingResult.hasErrors()) {
            Map<String, String> mapError= new HashMap<>();
            bindingResult.getFieldErrors().forEach(e -> {
                mapError.put(e.getField(), e.getDefaultMessage());
            });

            return APIResponse.responseBuilder(
                    mapError,
                    "Invalid input",
                    HttpStatus.BAD_REQUEST
            );
        }
        try{
            String username = authenticationRequest.getUsername();
            String password = authenticationRequest.getPassword();

            // Load user details
            User user = userService.findByUsername(username);
            if (user == null || !bCryptPasswordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("Invalid username or password");
            }

            String otp = userService.login(authenticationRequest);

            return APIResponse.responseBuilder(
                    null,
                    otp,
                    HttpStatus.OK
            );
        } catch (RuntimeException e) {
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        }catch (Exception e) {
            log.error("Error login: ", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @RequestMapping(value = "/api/auth/verify-otp", method = RequestMethod.POST)
    public ResponseEntity<?> verifyOtp(@RequestParam String username, @RequestParam String otp) {
        try {
            User user = userService.findByUsername(username);
            if (user == null || !userServiceImpl.verifyOtp(user, otp)) {
                throw new RuntimeException("Invalid OTP");
            }

            UserDetails userDetails = new UserDto(user);
            final String token = jwtTokenUtil.generateToken(userDetails);

            return APIResponse.responseBuilder(
                    token,
                    "Login successfully",
                    HttpStatus.OK
            );
        } catch (RuntimeException e) {
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        }catch (Exception e) {
            log.error("Error login: ", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    //Register
    @RequestMapping(value = "/api/auth/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@Valid @RequestBody UserRequest request,
                                      BindingResult bindingResult) {
        // Handle validation errors
        if (bindingResult.hasErrors()) {
            Map<String, String> mapError= new HashMap<>();
            bindingResult.getFieldErrors().forEach(e -> {
                mapError.put(e.getField(), e.getDefaultMessage());
            });

            return APIResponse.responseBuilder(
                    mapError,
                    "Invalid input",
                    HttpStatus.BAD_REQUEST
            );
        }
        try {
            String response = userService.registerUser(request);
            return APIResponse.responseBuilder(
                    null,
                    response,
                    HttpStatus.OK
            );
        } catch (RuntimeException e) {
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error register: ", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    //Forgot password
    @RequestMapping(value = "/api/auth/forgot-password", method = RequestMethod.POST)
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            String token = userService.generateForgotPasswordToken(email);
            String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;
            return APIResponse.responseBuilder(
                    resetLink,
                    "Password reset link generated successfully (Use the link within 30 minutes) (Method POST)",
                    HttpStatus.OK
            );
        } catch (RuntimeException e) {
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            log.error("Error generating password reset link: ", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @RequestMapping(value = "/api/auth/reset-password", method = RequestMethod.POST)
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            userService.resetPassword(token, newPassword);
            return APIResponse.responseBuilder(
                    null,
                    "Password reset successfully",
                    HttpStatus.OK
            );
        } catch (RuntimeException e) {
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error resetting password: ", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
//    @RequestMapping(value = "/api/auth/test", method = RequestMethod.GET)
//    public ResponseEntity<?> test() {
//        return APIResponse.responseBuilder(
//                null,
//                "Test",
//                HttpStatus.OK
//        );
//    }
}
