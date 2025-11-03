package com.api.app.demo_api.repository;

import com.api.app.demo_api.product.entity.Product;
import com.api.app.demo_api.product.repository.ProductRepository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test de la capa Repository.
 * Verifica que Spring Data JPA funciona correctamente y que podemos
 * persistir y recuperar entidades de la base de datos (H2 en memoria para tests).
 */
@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @AfterAll
    public static void afterTest(){
        System.out.println("Test hechos");
    }

    @Test
    void shouldSaveAndFindProduct() {
        // Arrange: crear producto de prueba
        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(20.00);

        // Act: guardar y recuperar
        Product saved = repository.save(product);
        Optional<Product> retrieved = repository.findById(saved.getId());


        // Assert: comprobar que se guardó y recuperó correctamente
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Laptop");
        assertEquals(product.getPrice(),retrieved.get().getPrice());
    }

    @Test
    void shouldReturnEmptyWhenProductNotFound() {
        Optional<Product> result = repository.findById(999L);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateProduct() {
        Product product = new Product();
        product.setName("Tablet");
        product.setPrice(100.0);
        Product saved = repository.save(product);

        saved.setPrice(120.0);
        repository.save(saved);

        Optional<Product> updated = repository.findById(saved.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getPrice()).isEqualTo(120.0);
    }

}
