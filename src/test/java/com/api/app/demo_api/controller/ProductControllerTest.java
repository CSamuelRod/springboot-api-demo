package com.api.app.demo_api.controller;

import com.api.app.demo_api.entity.Product;
import com.api.app.demo_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integración de la capa Controller.
 * Verifica endpoints REST completos, incluyendo seguridad y serialización JSON.
 * Usa MockMvc para simular peticiones HTTP.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll(); // limpiar datos antes de cada test
    }

    @Test
    void shouldReturnUnauthorizedWhenNoCredentials() throws Exception {
        // Testea que un endpoint protegido requiere autenticación
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnOkWhenUserAuthenticated() throws Exception {
        // Testea acceso a endpoint con usuario y contraseña válidos
        mockMvc.perform(get("/api/products").with(httpBasic("user", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateProductWhenAuthenticated() throws Exception {
        // Testea la creación de un producto via POST
        String productJson = """
            {
              "name": "Smartphone"
            }
            """;

        mockMvc.perform(post("/api/products")
                        .with(httpBasic("admin", "adminpass")) // usar rol ADMIN
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Smartphone"));
    }
}
