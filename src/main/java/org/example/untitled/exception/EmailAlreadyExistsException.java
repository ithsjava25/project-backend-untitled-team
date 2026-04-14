package org.example.untitled.exception;

public class EmailAlreadyExistsException extends AppException {
    private final String email;

    public EmailAlreadyExistsException(String email) {
        super("Email already exists");
        this.email = email;
    }

    public EmailAlreadyExistsException(String email, Throwable cause) {
        super("Email already exists", cause);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
