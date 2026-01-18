package com.auth_app.demo.exceptions;

/**
 * Standardized error codes for business exceptions.
 * Format: DOMAIN_SPECIFIC_ERROR
 */
public enum ErrorCode {

    // Authentication & Authorization
    AUTH_INVALID_CREDENTIALS("AUTH_001", "Invalid credentials provided"),
    AUTH_TOKEN_EXPIRED("AUTH_002", "Authentication token has expired"),
    AUTH_ACCESS_DENIED("AUTH_003", "Access denied to requested resource"),
    AUTH_ACCOUNT_LOCKED("AUTH_004", "Account is locked"),
    AUTH_ACCOUNT_DISABLED("AUTH_005", "Account is disabled"),

    // User Domain
    USER_NOT_FOUND("USER_001", "User not found"),
    USER_EMAIL_EXISTS("USER_002", "Email already exists"),
    USER_INVALID_PASSWORD("USER_003", "Invalid password format"),

    // Role Domain
    ROLE_NOT_FOUND("ROLE_001", "Role not found"),
    ROLE_ALREADY_ASSIGNED("ROLE_002", "Role already assigned to user"),

    // Validation
    VALIDATION_FAILED("VAL_001", "Validation failed"),

    // Generic
    RESOURCE_NOT_FOUND("RES_001", "Requested resource not found"),
    OPERATION_NOT_ALLOWED("RES_002", "Operation not allowed"),
    INTERNAL_ERROR("SYS_001", "Internal server error"),
    BAD_REQUEST("SYS_002", "Bad request");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
