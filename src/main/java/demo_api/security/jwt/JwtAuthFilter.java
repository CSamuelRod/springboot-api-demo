package com.api.app.demo_api.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro que:
 *  - Extrae el header Authorization: Bearer <token>
 *  - Valida el token con JwtService
 *  - Si es válido, construye una Authentication y la coloca en el SecurityContext
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String prefix = "Bearer ";

        if (authHeader == null || !authHeader.startsWith(prefix)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(prefix.length());
        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            // Token inválido o parseo fallido: no autenticamos y seguimos
            filterChain.doFilter(request, response);
            return;
        }

        // Si no hay autenticación en el contexto, comprobamos y la añadimos
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails.getUsername())) {
                // Si guardaste roles en claims bajo "roles", los extraemos
                var rolesClaim = jwtService.parseClaims(token).get("roles");
                List<SimpleGrantedAuthority> authorities = List.of();

                if (rolesClaim instanceof List) {
                    @SuppressWarnings("unchecked")
                    var roles = (List<String>) rolesClaim;
                    authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                }

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}