package com.auth_app.demo.services.impl;

import com.auth_app.demo.dtos.UserDto;
import com.auth_app.demo.dtos.UserResDto;
import com.auth_app.demo.entities.Role;
import com.auth_app.demo.entities.User;
import com.auth_app.demo.repositories.RoleRepository;
import com.auth_app.demo.repositories.UserRepository;
import com.auth_app.demo.services.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
   private final PasswordEncoder passwordEncoder;

    @Override
    public UserResDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDto.getEmail());
        }

        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .build();


        // Assign roles
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            Set<Role> roles = userDto.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        } else {
            // Assign default USER role
            roleRepository.findByName("USER")
                    .ifPresent(role -> user.getRoles().add(role));
        }

        User savedUser = userRepository.save(user);
        return mapToResDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResDto> getUserById(UUID id) {
        return userRepository.findById(id).map(this::mapToResDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::mapToResDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResDto updateUser(UUID id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.setName(userDto.getName());

        // Only update email if it's different and not already taken
        if (!user.getEmail().equals(userDto.getEmail())) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + userDto.getEmail());
            }
            user.setEmail(userDto.getEmail());
        }

        // Only update password if provided
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        // Update roles if provided
        if (userDto.getRoles() != null) {
            Set<Role> roles = userDto.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);
        return mapToResDto(savedUser);
    }

    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserResDto addRoleToUser(UUID userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

        user.addRole(role);
        User savedUser = userRepository.save(user);
        return mapToResDto(savedUser);
    }

    @Override
    public UserResDto removeRoleFromUser(UUID userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

        user.removeRole(role);
        User savedUser = userRepository.save(user);
        return mapToResDto(savedUser);
    }

    /**
     * Maps a User entity to UserResDto.
     */
    private UserResDto mapToResDto(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserResDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .emailVerified(user.getEmailVerified())
                .provider(user.getProvider())
                .roles(roleNames)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
