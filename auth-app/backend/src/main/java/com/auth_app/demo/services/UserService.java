package com.auth_app.demo.services;

import com.auth_app.demo.dtos.UserDto;
import com.auth_app.demo.dtos.UserResDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for User operations.
 */
public interface UserService {

    /**
     * Create a new user.
     */
    UserResDto createUser(UserDto userDto);

    /**
     * Get user by ID.
     */
    Optional<UserResDto> getUserById(UUID id);

    /**
     * Get user by email.
     */
    Optional<UserResDto> getUserByEmail(String email);

    /**
     * Get all users.
     */
    List<UserResDto> getAllUsers();

    /**
     * Update an existing user.
     */
    UserResDto updateUser(UUID id, UserDto userDto);

    /**
     * Delete a user by ID.
     */
    void deleteUser(UUID id);

    /**
     * Check if a user exists by email.
     */
    boolean existsByEmail(String email);

    /**
     * Add a role to a user.
     */
    UserResDto addRoleToUser(UUID userId, String roleName);

    /**
     * Remove a role from a user.
     */
    UserResDto removeRoleFromUser(UUID userId, String roleName);
}
