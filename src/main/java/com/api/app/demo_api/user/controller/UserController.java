package com.api.app.demo_api.user.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.api.app.demo_api.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.api.app.demo_api.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto dto) {
        User User = toEntity(dto);
        User saved = userService.saveUser(User);
        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Conversión Entity <-> DTO
    private UserDto toDto(User User) {
        UserDto dto = new UserDto();
        dto.setId(User.getId());
        dto.setName(User.getName());
        return dto;
    }

    private User toEntity(UserDto dto) {
        User User = new User();
        User.setName(dto.getName());
        return User;
    }
}
