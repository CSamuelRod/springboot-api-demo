package com.api.app.demo_api.common.security;

import com.api.app.demo_api.jwtConfig.jwt.JwtAuthFilter;
import com.api.app.demo_api.jwtConfig.jwt.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public SecurityConfig(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    // Bean para autenticar usuarios en login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Bean para encriptar contraseñas usando BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuración principal de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Filtro que valida JWT y setea la autenticación en SecurityContext
        JwtAuthFilter jwtFilter = new JwtAuthFilter(jwtUtil);

        http
                // Deshabilitamos CSRF porque es una API REST stateless
                .csrf(csrf -> csrf.disable())

                // No mantenemos sesión en servidor (stateless) → cada request valida token
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos: registro/login, swagger y recursos públicos
                        .requestMatchers("/auth/**", "/public/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Consola H2 (solo para desarrollo)
                        .requestMatchers("/h2-console/**").permitAll()

                        // Actuator: health/info público, resto solo ADMIN
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                        // Endpoints de productos → solo ADMIN
                        .requestMatchers("/api/products/**").hasRole("ADMIN")

                        // Endpoints de órdenes → cualquier usuario autenticado
                        .requestMatchers("/orders/**").authenticated()

                        // Resto de endpoints → autenticación requerida
                        .anyRequest().authenticated()
                )

                // Headers adicionales
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()) // Necesario para H2 console
                );

        // Insertamos el filtro JWT **antes** del filtro de username/password de Spring
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}