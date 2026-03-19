package com.api.app.demo_api.order.repository;

import com.api.app.demo_api.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}