package com.api.app.demo_api.common.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class UserDto {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;
}
