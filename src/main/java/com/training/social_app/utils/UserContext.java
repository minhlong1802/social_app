package com.training.social_app.utils;


import com.training.social_app.dto.request.UserRequest;
import com.training.social_app.dto.response.UserDto;

public class UserContext {
    private static final ThreadLocal<UserDto> userHolder = new ThreadLocal<>();

    private static final ThreadLocal<UserRequest> createUserHolder = new ThreadLocal<>();

    public static void setUser(UserDto userDto) {
        userHolder.set(userDto);
    }

    public static UserDto getUser() {
        return userHolder.get();
    }

    public static void clear() {
        userHolder.remove();
    }

    public static UserRequest getCreateUser() { return createUserHolder.get(); }
    }