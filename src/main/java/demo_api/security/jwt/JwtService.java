package com.api.app.demo_api.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * Servicio para generar y validar JWT usando la librería JJWT.
 * - Genera tokens con claims adicionales.
 * - Valida firma y expiración.
 *
 * NOTAS:
 * - El secret debe tener suficiente entropía/longitud para HMAC (256 bits mínimo recomendado).
 * - En producción guarda el secret en variables de entorno o Vault, no en properties.
 */
@Service
public class JwtService {

    // Inyectamos la propiedad jwt.secret desde application.properties o variable de entorno
    @Value("${jwt.secret}")
    private String secret;

    // Tiempo de expiración en milisegundos
    @Value("${jwt.expiration-ms:3600000}") // 1 hora por defecto
    private long expirationMs;

    // SecretKey construido a partir del secret (Keys.hmacShaKeyFor valida tamaño)
    private SecretKey key;

    @PostConstruct
    public void init() {
        // Convertimos la String a bytes usando UTF-8 y creamos la SecretKey.
        // Si el secret es Base64, decodifícalo antes: Decoders.BASE64.decode(secret)
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un token JWT con subject=username y los claims adicionales que pases.
     */
    public String generateToken(String username, Map<String, Object> extraClaims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .addClaims(extraClaims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Parsea claims y valida firma. Lanza excepción (JwtException) si el token es inválido.
     */
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token, String username) {
        return username != null && username.equals(extractUsername(token)) && !isTokenExpired(token);
    }
}