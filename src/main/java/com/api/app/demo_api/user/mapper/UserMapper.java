package com.api.app.demo_api.user.mapper;

import com.api.app.demo_api.user.dto.UserRequest;
import com.api.app.demo_api.user.dto.UserResponse;
import com.api.app.demo_api.user.entity.User;
import com.api.app.demo_api.user.entity.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().toString() // si usas enum Role
        );
    }

    public User toEntity(UserRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(request.password()); // recuerda cifrar con BCrypt
        // si role es enum
        user.setRole(Role.valueOf(request.role().toUpperCase()));
        return user;
    }
}