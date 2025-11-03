package com.api.app.demo_api.jwtConfig.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    // getters y setters
}