package com.api.app.demo_api.order.controller;

import com.api.app.demo_api.order.dto.CreateOrderRequest;
import com.api.app.demo_api.order.dto.OrderResponse;
import com.api.app.demo_api.order.entity.Order;
import com.api.app.demo_api.order.mapper.OrderMapper;
import com.api.app.demo_api.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody @Valid CreateOrderRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        Order order = orderService.createOrder(request, username);

        return ResponseEntity.ok(orderMapper.toResponse(order));
    }
}
