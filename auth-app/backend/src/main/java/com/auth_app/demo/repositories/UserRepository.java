package com.auth_app.demo.repositories;

import com.auth_app.demo.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.antlr.v4.runtime.misc.MultiMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(@NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email);

    Optional<User> findByEmail(String email);
}
