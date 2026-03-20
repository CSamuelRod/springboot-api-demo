package com.api.app.demo_api.jwtConfig.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JwtUtil - Clase utilitaria para crear, validar y leer JWTs.
 * Adaptada para un solo rol por usuario (ADMIN o USER).
 */
@Component
public class JwtUtil {

    private final Key key; // clave secreta para firmar los tokens
    private final long validityMillis = 1000L * 60 * 60; // 1 hora de validez

    /**
     * Constructor que obtiene la clave secreta desde application.properties
     */
    public JwtUtil(Environment env) {
        String secret = env.getProperty("jwt.secret");
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("jwt.secret no definido");
        }
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException("jwt.secret debe tener al menos 32 bytes para HS256");
        }
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    /**
     * Genera un token JWT para un usuario y su rol.
     * @param username Nombre del usuario
     * @param role Rol del usuario (ADMIN o USER)
     * @return JWT como String
     */
    public String generateToken(String username, String role) {
        Date now = new Date();
        // Guardamos el rol como una lista con un solo elemento para mantener compatibilidad
        Map<String, Object> claims = Map.of("roles", List.of(role));

        return Jwts.builder()
                .setSubject(username)         // username en "sub"
                .setClaims(claims)            // rol en "roles"
                .setIssuedAt(now)              // fecha de emisión
                .setExpiration(new Date(now.getTime() + validityMillis)) // expiración
                .signWith(key, SignatureAlgorithm.HS256) // firmar con HS256
                .compact();
    }

    /**
     * Valida si el token es correcto y no ha expirado.
     * @param token JWT
     * @return true si válido, false si inválido
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // opcional: loguear ex.getMessage()
            return false;
        }
    }

    /**
     * Extrae el username del token (campo "sub")
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extrae el rol del token.
     * Como solo hay un rol, devolvemos el primer elemento de la lista.
     */
    @SuppressWarnings("unchecked")
    public String extractRole(String token) {
        var body = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
        Object rolesObj = body.get("roles");
        if (rolesObj instanceof List<?> rolesList && !rolesList.isEmpty()) {
            return rolesList.get(0).toString(); // tomamos el único rol
        }
        return null;
    }

    /**
     * Extrae todos los roles como lista (opcional, si algún día hay varios)
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        var body = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
        Object rolesObj = body.get("roles");
        if (rolesObj instanceof List<?> list) {
            return ((List<?>) list).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}