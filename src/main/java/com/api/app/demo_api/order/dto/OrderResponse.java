package com.api.app.demo_api.order.dto;

import java.util.List;

public record OrderResponse(
        Long id,
        String status,
        List<OrderItemResponse> items
) {}