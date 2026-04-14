package org.example.untitled.exception;

public class UserHasActiveCasesException extends AppException {

    public UserHasActiveCasesException() {
        super("User has active cases");
    }

    public UserHasActiveCasesException(Throwable cause) {
        super("User has active cases", cause);
    }
}
