package com.api.app.demo_api.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank
        String username,

        @NotBlank
        String password,

        @NotBlank
        String role
) {}