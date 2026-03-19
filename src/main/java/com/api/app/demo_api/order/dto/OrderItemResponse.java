package com.api.app.demo_api.order.dto;

public record OrderItemResponse(
        Long productId,
        Integer quantity,
        Double price
) {}