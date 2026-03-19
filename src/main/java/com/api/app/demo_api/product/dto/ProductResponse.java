package com.api.app.demo_api.product.dto;

public record ProductResponse(
        Long id,
        String name,
        Double price
) {}