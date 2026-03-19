package com.api.app.demo_api.user.dto;

public record UserResponse(
        Long id,
        String username,
        String role
) {}