package com.api.app.demo_api.security.controller;

import com.api.app.demo_api.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authManager,
                          JwtService jwtService,
                          org.springframework.security.core.userdetails.UserDetailsService uds) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userDetailsService = uds;
    }

    @PostMapping("/login")
    public Map<String,String> login(@RequestBody AuthRequest req) {
        var token = new UsernamePasswordAuthenticationToken(req.username(), req.password());
        authManager.authenticate(token); // lanza excepción si falla

        UserDetails ud = userDetailsService.loadUserByUsername(req.username());
        var roles = ud.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList());

        String jwt = jwtService.generateToken(req.username(), Map.of("roles", roles));
        return Map.of("token", jwt);
    }

    public record AuthRequest(String username, String password) {}
}
