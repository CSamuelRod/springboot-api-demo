package com.api.app.demo_api.user.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.api.app.demo_api.user.entity.UserAPI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.app.demo_api.common.dto.UserApiDto;
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
    public List<UserApiDto> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserApiDto> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserApiDto> createUser(@Valid @RequestBody UserApiDto dto) {
        UserAPI userAPI = toEntity(dto);
        UserAPI saved = userService.saveUser(userAPI);
        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Conversión Entity <-> DTO
    private UserApiDto toDto(UserAPI userAPI) {
        UserApiDto dto = new UserApiDto();
        dto.setId(userAPI.getId());
        dto.setName(userAPI.getName());
        return dto;
    }

    private UserAPI toEntity(UserApiDto dto) {
        UserAPI userAPI = new UserAPI();
        userAPI.setName(dto.getName());
        return userAPI;
    }
}
