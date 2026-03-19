package com.api.app.demo_api.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.app.demo_api.product.entity.Product;

import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product,Long> {
}
