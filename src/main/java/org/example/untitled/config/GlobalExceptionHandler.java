package org.example.untitled.config;

import org.example.untitled.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String ERROR_PAGE = "errorpage";
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleUserAlreadyExists(UserAlreadyExistsException ex) {
        log.warn("User already exists");
        return ERROR_PAGE;
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFound(UserNotFoundException ex) {
        log.warn("User not found");
        return ERROR_PAGE;
    }

    @ExceptionHandler(CaseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCaseNotFound(CaseNotFoundException ex) {
        log.warn("Case not found");
        return ERROR_PAGE;
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        log.warn("Email already exists");
        return ERROR_PAGE;
    }

    @ExceptionHandler(UserHasActiveCasesException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleUserHasActiveCases(UserHasActiveCasesException ex) {
        log.warn("User has active cases");
        return ERROR_PAGE;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        return ERROR_PAGE;
    }
}
