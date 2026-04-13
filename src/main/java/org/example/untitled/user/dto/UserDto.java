package org.example.untitled.user.dto;

import jakarta.validation.constraints.Email;
import org.example.untitled.user.Role;

import java.time.LocalDateTime;

public record UserDto (
        Long id,
        String username,
        @Email
        String email,
        Role role,
        LocalDateTime createdAt
){ }
