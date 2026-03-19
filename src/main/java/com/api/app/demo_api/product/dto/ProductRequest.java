package com.api.app.demo_api.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
        @NotBlank
        String name,

        @NotNull
        @Min(0)
        Double price,

        @NotNull
        @Min(0)
        Integer stock
) {}