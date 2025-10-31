package com.api.app.demo_api.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data // Lombok genera getters, setters, toString, equals y hashCode
@Entity // Indica que esta clase es una tabla de la DB
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID autoincremental
    private Long id;

    private String name;

    private Double price;

    private Integer stock;
}
