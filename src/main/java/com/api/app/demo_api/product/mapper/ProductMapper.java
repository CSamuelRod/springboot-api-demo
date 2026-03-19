package com.api.app.demo_api.product.mapper;

import com.api.app.demo_api.product.dto.ProductRequest;
import com.api.app.demo_api.product.dto.ProductResponse;
import com.api.app.demo_api.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice()
        );
    }

    public Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());
        return product;
    }
}