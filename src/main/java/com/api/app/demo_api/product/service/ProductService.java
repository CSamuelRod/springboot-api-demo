package com.api.app.demo_api.product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.api.app.demo_api.product.entity.Product;
import com.api.app.demo_api.product.repository.ProductRepository;

import com.api.app.demo_api.product.entity.Product;
import com.api.app.demo_api.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

        public Product saveProduct(Product product) {
            return productRepository.save(product);
        }

        public void deleteProduct(Long id) {
            productRepository.deleteById(id);
        }

}
