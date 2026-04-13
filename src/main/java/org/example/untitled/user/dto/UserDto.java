package org.example.untitled.user.dto;

import org.example.untitled.user.Role;

import java.time.LocalDateTime;

public record UserDto (
        Long id,
        String username,
        String email,
        Role role,
        LocalDateTime createdAt
){ }
