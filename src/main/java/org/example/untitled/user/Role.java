package org.example.untitled.user;

import java.util.Optional;

public enum Role {
    USER,
    HANDLER,
    SUPERVISOR,
    ADMIN;

    public static Optional<Role> fromAuthority(String authority) {
        if (authority == null) return Optional.empty();
        try {
            return Optional.of(Role.valueOf(authority.replace("ROLE_", "")));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
