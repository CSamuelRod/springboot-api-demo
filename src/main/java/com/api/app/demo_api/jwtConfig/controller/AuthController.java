package com.api.app.demo_api.jwtConfig.controller;

import com.api.app.demo_api.jwtConfig.dtos.AuthRequest;
import com.api.app.demo_api.jwtConfig.dtos.AuthResponse;
import com.api.app.demo_api.jwtConfig.dtos.RegisterRequest;
import com.api.app.demo_api.jwtConfig.jwt.JwtUtil;
import com.api.app.demo_api.user.entity.User;
import com.api.app.demo_api.user.entity.enums.Role;
import com.api.app.demo_api.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, UserRepository userRepo, PasswordEncoder encoder) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {

        // 🔹 Autenticación con Spring Security
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        // 🔹 Obtener el único rol del usuario
        String role = auth.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority()) // "ADMIN" o "USER"
                .findFirst() // tomamos el primero (solo hay uno)
                .orElseThrow(() -> new RuntimeException("No role assigned to user"));

        // 🔹 Generar token JWT con username y rol
        String token = jwtUtil.generateToken(req.getUsername(), role);

        // 🔹 Devolver token en la respuesta
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest r) {
        if (userRepo.existsByUsername(r.username())) {
            return ResponseEntity.badRequest().body("Username exists");
        }

        User u = new User();
        u.setUsername(r.username());
        u.setPassword(encoder.encode(r.password()));

        // Convertimos el string a enum
        try {
            u.setRole(Role.valueOf(r.role().toUpperCase()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role");
        }

        userRepo.save(u);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
