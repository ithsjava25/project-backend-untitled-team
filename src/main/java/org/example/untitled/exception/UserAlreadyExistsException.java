package org.example.untitled.exception;

public class UserAlreadyExistsException extends AppException {
    private final String username;

    public UserAlreadyExistsException(String username) {
        super("User already exists");
        this.username = username;
    }

    public UserAlreadyExistsException(String username, Throwable cause) {
        super("User already exists", cause);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
