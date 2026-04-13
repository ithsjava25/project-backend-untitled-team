package org.example.untitled.exception;

public class UserNotFoundException extends AppException {
    public UserNotFoundException(String username) {
        super("User not found: " + username);
    }
}
