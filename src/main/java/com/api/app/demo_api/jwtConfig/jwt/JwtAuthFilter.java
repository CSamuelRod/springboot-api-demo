package com.api.app.demo_api.jwtConfig.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * JwtAuthFilter - Filtro que valida JWT y setea la autenticación en Spring Security
 * Versión simplificada para un solo rol (ADMIN o USER)
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();
        log.debug("JwtAuthFilter - request to: {} header Authorization present? {}", path, req.getHeader("Authorization") != null);

        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7); // quitar "Bearer "

            try {
                if (jwtUtil.validateToken(token)) {

                    // ✅ Extraer username del token
                    String username = jwtUtil.extractUsername(token);
                    log.debug("JwtAuthFilter - token válido para: {}", username);

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                        // 🔹 Extraer rol (solo uno) desde el token
                        String role = jwtUtil.extractRole(token); // <-- aquí llamamos al nuevo método

                        // 🔹 Crear GrantedAuthority a partir del rol
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

                        // 🔹 Crear token de autenticación para Spring
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(username, null, java.util.List.of(authority));

                        // 🔹 Asociar detalles del request (opcional)
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

                        // 🔹 Setear el usuario autenticado en el contexto de Spring
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        log.debug("JwtAuthFilter - Authentication set in SecurityContext for user: {} with role: {}", username, role);
                    }

                } else {
                    log.debug("JwtAuthFilter - token no válido");
                }
            } catch (Exception ex) {
                log.debug("JwtAuthFilter - error validando token: {}", ex.getMessage());
            }

        } else {
            log.debug("JwtAuthFilter - no Authorization header or not Bearer");
        }

        // Continuar con la ejecución del filtro
        chain.doFilter(req, res);
    }
}