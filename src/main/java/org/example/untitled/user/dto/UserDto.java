package org.example.untitled.user.dto;

import org.example.untitled.user.Role;

import java.time.Instant;

public record UserDto (
        Long id,
        String username,
        String email,
        Role role,
        Instant createdAt
){ }
