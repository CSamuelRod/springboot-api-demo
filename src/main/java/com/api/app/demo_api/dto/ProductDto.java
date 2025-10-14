package com.api.app.demo_api.dto;


import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ProductDto {

    private Long id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Price is mandatory")
    private Double price;
}