package org.example.untitled.exception;

public class CaseNotFoundException extends AppException {
    public CaseNotFoundException(Long id) {
        super("Case not found with id: " + id);
    }
}
