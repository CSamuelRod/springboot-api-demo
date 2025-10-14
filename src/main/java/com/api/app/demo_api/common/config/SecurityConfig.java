package com.api.app.demo_api.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Usuarios en memoria (solo para desarrollo/demo)
    @Bean
    public UserDetailsService users(PasswordEncoder encoder) {
        var user = User.withUsername("user")
                .password(encoder.encode("password"))
                .roles("USER")
                .build();

        var admin = User.withUsername("admin")
                .password(encoder.encode("adminpass"))
                .roles("USER","ADMIN","ACTUATOR")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Regla de seguridad: permitir swagger + health/info, securizar el resto
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
http
    .csrf(csrf -> csrf.disable()) // ✅ reemplaza a .csrf().disable()
        .authorizeHttpRequests(auth -> auth
        // Permitir acceso publico a swagger UI (UI y recursos estáticos)
        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
        // Permitir health y info públicamente (puedes cambiar a auth si lo prefieres)
        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
        // Si quieres proteger endpoints actuator sensibles, podrías:
        // .requestMatchers("/actuator/**").hasRole("ACTUATOR")
        // Protege el resto
        .anyRequest().authenticated()
        )
          .httpBasic(Customizer.withDefaults()); // autenticación básica
        return http.build();
    }
}
