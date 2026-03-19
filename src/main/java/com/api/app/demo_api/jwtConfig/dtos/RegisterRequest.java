package com.api.app.demo_api.jwtConfig.dtos;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String role // recibimos como string y luego lo convertimos a enum
) {}