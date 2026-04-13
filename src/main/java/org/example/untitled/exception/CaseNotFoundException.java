package org.example.untitled.exception;

public class CaseNotFoundException extends AppException {
    private final Long id;

    public CaseNotFoundException(Long id) {
        super("Case not found");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
