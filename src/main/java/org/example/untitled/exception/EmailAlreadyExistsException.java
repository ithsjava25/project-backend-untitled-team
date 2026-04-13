package org.example.untitled.exception;

public class EmailAlreadyExistsException extends AppException {
    public EmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }
}
