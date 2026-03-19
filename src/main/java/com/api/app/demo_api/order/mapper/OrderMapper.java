package com.api.app.demo_api.order.mapper;

import com.api.app.demo_api.order.dto.OrderItemResponse;
import com.api.app.demo_api.order.dto.OrderResponse;
import com.api.app.demo_api.order.entity.Order;
import com.api.app.demo_api.order.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getItems().stream()
                        .map(this::toItemResponse)
                        .toList()
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getProductId(),
                item.getQuantity(),
                item.getPrice()
        );
    }
}
