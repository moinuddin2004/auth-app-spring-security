package com.auth_app.demo.dtos;

import com.auth_app.demo.enums.AuthProvider;
import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Response DTO for User - used when returning user data to clients.
 * Excludes sensitive information like password.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResDto {

    private UUID id;
    private String name;
    private String email;
    private Boolean emailVerified;
    private AuthProvider provider;
    private Set<String> roles;
    private Instant createdAt;
    private Instant updatedAt;
}
