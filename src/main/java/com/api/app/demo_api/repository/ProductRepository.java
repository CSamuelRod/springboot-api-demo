package com.api.app.demo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.app.demo_api.entity.Product;


public interface ProductRepository extends JpaRepository<Product,Long> {

}
