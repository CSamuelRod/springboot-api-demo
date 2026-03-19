package com.api.app.demo_api.order.service;

import com.api.app.demo_api.order.dto.CreateOrderRequest;
import com.api.app.demo_api.order.entity.Order;
import com.api.app.demo_api.order.entity.OrderItem;
import com.api.app.demo_api.order.repository.OrderRepository;
import com.api.app.demo_api.product.entity.Product;
import com.api.app.demo_api.product.service.ProductService;
import com.api.app.demo_api.user.entity.User;
import com.api.app.demo_api.user.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final UserService userService;

    @Transactional
    public Order createOrder(@Valid CreateOrderRequest request, String username) {

        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = request.items().stream()
                .map(itemReq -> {

                    Product product = productService.findById(itemReq.productId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Product not found: " + itemReq.productId()
                            ));

                    OrderItem item = new OrderItem();
                    item.setProductId(product.getId());
                    item.setQuantity(itemReq.quantity());
                    item.setPrice(product.getPrice());
                    item.setOrder(order);

                    return item;
                })
                .toList();

        order.setItems(items);

        return orderRepository.save(order);
    }
}
