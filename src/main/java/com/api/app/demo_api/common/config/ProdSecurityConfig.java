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
import org.springframework.security.config.Customizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.List;

/**
 * Configuración para producción.
 * - Usa JWT igual que en base
 * - Requiere (preferiblemente) un UserDetailsService potente (JPA)
 * - Exponemos PasswordEncoder y AuthenticationManager para que AuthController funcione
 * - CORS configurado según dominios productivos
 *
 * NOTA: en prod debes proveer un UserDetailsService (por ejemplo JpaUserDetailsService).
 * Si NO existe, se crea un InMemoryUserDetailsManager como fallback (esto es sólo para evitar
 * errores en despliegues tempranos; idealmente no dejar fallback en producción).
 */
@Configuration
@EnableMethodSecurity
@Profile("prod")
public class ProdSecurityConfig {

    /**
     * PasswordEncoder (BCrypt)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Fallback UserDetailsService SOLO si no existe otro (como uno que use JPA).
     * En producción lo ideal es implementar tu propio UserDetailsService que lea usuarios de BD.
     */
    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService fallbackUserDetailsService(PasswordEncoder encoder) {
        // Usuario de emergencia (no recomendado mantener en producción)
        var admin = User.withUsername("admin")
                .password(encoder.encode("adminpass"))
                .roles("ADMIN", "ACTUATOR")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    /**
     * AuthenticationManager expuesto para que AuthController pueda inyectarlo.
     * Depende de que exista un UserDetailsService en el contexto.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    /**
     * SecurityFilterChain para producción.
     * - JWT: se añade JwtAuthFilter
     * - Se permite /auth/** (login) y actuator health/info públicamente
     * - Resto autenticado
     * - Sessions stateless
     */
    @Bean
    public SecurityFilterChain prodFilterChain(HttpSecurity http,
                                               JwtService jwtService,
                                               UserDetailsService userDetailsService) throws Exception {

        JwtAuthFilter jwtFilter = new JwtAuthFilter(jwtService, userDetailsService);

        http
                // Si usas JWT, la API es stateless -> disabling CSRF es habitual
                .csrf(csrf -> csrf.disable())

                // CORS: ajustar orígenes permitidos en producción
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/actuator/health", "/actuator/info").permitAll()
                        .anyRequest().authenticated()
                )

                // Stateless (JWT)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Añadimos el filtro JWT antes del filtro estándar
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Cors configuration (ajusta dominios).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Ajusta el origen real de tu frontend en producción
        configuration.setAllowedOrigins(List.of("https://tu-dominio.com"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
