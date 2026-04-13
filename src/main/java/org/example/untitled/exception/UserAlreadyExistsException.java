package org.example.untitled.exception;

public class UserAlreadyExistsException extends AppException {
    private final String username;

    public UserAlreadyExistsException(String username) {
        super("User already exists");
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
