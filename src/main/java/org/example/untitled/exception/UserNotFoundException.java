package org.example.untitled.exception;

public class UserNotFoundException extends AppException {
    private final String username;

    public UserNotFoundException(String username) {
        super("User not found");
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
