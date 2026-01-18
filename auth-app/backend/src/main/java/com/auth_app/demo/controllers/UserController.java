package com.auth_app.demo.controllers;

import com.auth_app.demo.dtos.UserDto;
import com.auth_app.demo.dtos.UserResDto;
import com.auth_app.demo.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserResDto createdUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResDto> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResDto> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserResDto>> getAllUsers() {
        List<UserResDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResDto> updateUser(@PathVariable UUID id, @Valid @RequestBody UserDto userDto) {
        UserResDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/roles/{roleName}")
    public ResponseEntity<UserResDto> addRoleToUser(@PathVariable UUID id, @PathVariable String roleName) {
        UserResDto updatedUser = userService.addRoleToUser(id, roleName);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    public ResponseEntity<UserResDto> removeRoleFromUser(@PathVariable UUID id, @PathVariable String roleName) {
        UserResDto updatedUser = userService.removeRoleFromUser(id, roleName);
        return ResponseEntity.ok(updatedUser);
    }
}
