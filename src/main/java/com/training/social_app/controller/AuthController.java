package com.training.social_app.controller;

import com.training.social_app.dto.request.LoginRequest;
import com.training.social_app.dto.request.UserRequest;
import com.training.social_app.dto.response.APIResponse;
import com.training.social_app.entity.User;
import com.training.social_app.service.UserService;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //Login
    @RequestMapping(value = "/api/auth/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody LoginRequest authenticationRequest,
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

            // Generate token
            final String token = jwtTokenUtil.generateToken(user);

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
        }
    }
}
