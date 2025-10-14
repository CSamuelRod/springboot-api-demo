package com.api.app.demo_api.service;

import com.api.app.demo_api.entity.Product;
import com.api.app.demo_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test unitario de la capa Service.
 * No necesita base de datos real, usamos Mockito para simular el repository.
 * Verifica la lógica de negocio aislada.
 */
class ProductServiceTest {

    private ProductRepository repository;
    private ProductService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(ProductRepository.class);
        service = new ProductService(repository);
    }

    @Test
    void shouldReturnProductWhenExists() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Optional<Product> result = service.getProductById(1L);


        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Laptop");
    }
}
