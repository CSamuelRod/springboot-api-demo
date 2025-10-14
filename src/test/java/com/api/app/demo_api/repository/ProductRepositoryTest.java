package com.api.app.demo_api.repository;

import com.api.app.demo_api.product.entity.Product;
import com.api.app.demo_api.product.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de la capa Repository.
 * Verifica que Spring Data JPA funciona correctamente y que podemos
 * persistir y recuperar entidades de la base de datos (H2 en memoria para tests).
 */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Test
    void shouldSaveAndFindProduct() {
        // Arrange: crear producto de prueba
        Product product = new Product();
        product.setName("Laptop");

        // Act: guardar y recuperar
        Product saved = repository.save(product);
        Optional<Product> retrieved = repository.findById(saved.getId());

        // Assert: comprobar que se guardó y recuperó correctamente
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Laptop");
    }
}
