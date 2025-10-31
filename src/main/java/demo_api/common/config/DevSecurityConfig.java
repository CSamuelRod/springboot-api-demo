package com.api.app.demo_api.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("dev")
public class DevSecurityConfig {

    @Bean
    public SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // H2 console necesita csrf deshabilitado
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // H2 console en iframe
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll() // H2 console libre
                        .anyRequest().permitAll() // resto libre en dev
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
