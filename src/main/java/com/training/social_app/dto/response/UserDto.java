package com.training.social_app.dto.response;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class UserDto implements UserDetails {
    private int userId;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
}
