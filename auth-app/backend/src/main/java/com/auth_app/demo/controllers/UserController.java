package com.auth_app.demo.controllers;

import com.auth_app.demo.common.ApiResponse;
import com.auth_app.demo.dtos.UserDto;
import com.auth_app.demo.dtos.UserResDto;
import com.auth_app.demo.exceptions.EntityNotFoundException;
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
    public ResponseEntity<ApiResponse<UserResDto>> createUser(@Valid @RequestBody UserDto userDto) {
        UserResDto createdUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(createdUser, "User created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResDto>> getUserById(@PathVariable UUID id) {
        UserResDto user = userService.getUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResDto>> getUserByEmail(@PathVariable String email) {
        UserResDto user = userService.getUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User", email));
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResDto>>> getAllUsers() {
        List<UserResDto> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResDto>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserDto userDto) {
        UserResDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "User updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }

    @PostMapping("/{id}/roles/{roleName}")
    public ResponseEntity<ApiResponse<UserResDto>> addRoleToUser(
            @PathVariable UUID id,
            @PathVariable String roleName) {
        UserResDto updatedUser = userService.addRoleToUser(id, roleName);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Role added successfully"));
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    public ResponseEntity<ApiResponse<UserResDto>> removeRoleFromUser(
            @PathVariable UUID id,
            @PathVariable String roleName) {
        UserResDto updatedUser = userService.removeRoleFromUser(id, roleName);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Role removed successfully"));
    }
}

