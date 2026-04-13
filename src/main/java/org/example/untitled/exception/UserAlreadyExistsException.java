package org.example.untitled.exception;

public class UserAlreadyExistsException extends AppException {
    public UserAlreadyExistsException(String username) {
        super("User already exists: " + username);
    }
}
