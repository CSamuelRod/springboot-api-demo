package com.api.app.demo_api.user.mapper;


import com.api.app.demo_api.user.dto.UserResponse;
import com.api.app.demo_api.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().toString()
        );
    }
}