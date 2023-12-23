package com.deepak.mongoreactive.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        LOGGER.error("An error occurred: {}", ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred. Please try again later.");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        if (ex.getStatusCode().value() != 404) {
            ex.printStackTrace();
        }
        LOGGER.error("A ResponseStatusException occurred: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(ResponseStatusException ex) {
        LOGGER.error("Requested User Not found: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleDuplicateUser(UserAlreadyExistsException ex) {
        String errorMessage = "User already exists: " + ex.getMessage();
        LOGGER.error(errorMessage);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
    }

    @ExceptionHandler(CannotUpdatePhoneNumberException.class)
    public ResponseEntity<String> handleUpdatePhoneNumberUsedByAnotherUser(CannotUpdatePhoneNumberException ex) {
        String errorMessage = "User with phone number exists: " + ex.getMessage();
        LOGGER.error(errorMessage);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
    }
}