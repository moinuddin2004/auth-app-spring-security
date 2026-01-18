package com.auth_app.demo.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested entity is not found.
 */
public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(String entityName, Object identifier) {
        super(
            ErrorCode.RESOURCE_NOT_FOUND,
            HttpStatus.NOT_FOUND,
            String.format("%s not found with identifier: %s", entityName, identifier)
        );
    }

    public EntityNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, HttpStatus.NOT_FOUND, message);
    }
}
