package org.example.untitled.user;

public enum Role {
    USER,
    HANDLER,
    SUPERVISOR,
    ADMIN;

    public static Role fromAuthority(String authority) {
        try {
            return Role.valueOf(authority.replace("ROLE_", ""));
        } catch (IllegalArgumentException e) {
            return Role.USER;
        }
    }
}
