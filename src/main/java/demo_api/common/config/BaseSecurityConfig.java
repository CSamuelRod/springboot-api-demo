package com.api.app.demo_api.common.config;

import com.api.app.demo_api.security.jwt.JwtAuthFilter;
import com.api.app.demo_api.security.jwt.JwtService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Configuración base (dev / test / default).
 * - JWT + filtro JwtAuthFilter
 * - Usuarios en memoria (solo por dev/test)
 * - Sessions stateless
 */
@Configuration
@EnableMethodSecurity
@Profile({"dev", "test", "default"}) // se activa en dev/test o cuando no se especifica profile
public class BaseSecurityConfig {

    /**
     * Usuario en memoria (solo para desarrollo / pruebas).
     * Se crea solo si no existe otro UserDetailsService en el contexto.
     */
    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService users(PasswordEncoder encoder) {
        var user = User.withUsername("user")
                .password(encoder.encode("password"))
                .roles("USER")
                .build();

        var admin = User.withUsername("admin")
                .password(encoder.encode("adminpass"))
                .roles("USER", "ADMIN", "ACTUATOR")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    /**
     * BCrypt PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * SecurityFilterChain: añade JwtAuthFilter y configura rutas públicas/protegidas.
     * - /auth/** -> permitAll (login)
     * - /actuator/** -> permitAll (health/info)
     * - resto -> authenticated
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtService jwtService,
                                                   UserDetailsService userDetailsService) throws Exception {

        JwtAuthFilter jwtFilter = new JwtAuthFilter(jwtService, userDetailsService);

        http
                .csrf(csrf -> csrf.disable()) // API stateless -> CSRF deshabilitado
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/actuator/health", "/actuator/info").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Insertar filtro JWT antes del UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * AuthenticationManager expuesto como Bean (necesario para AuthController).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
