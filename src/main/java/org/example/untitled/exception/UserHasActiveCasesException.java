package org.example.untitled.exception;

public class UserHasActiveCasesException extends AppException {
    private final String username;

    public UserHasActiveCasesException(String username) {
        super("User has active cases");
        this.username = username;
    }

    public UserHasActiveCasesException(String username, Throwable cause) {
        super("User has active cases", cause);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
