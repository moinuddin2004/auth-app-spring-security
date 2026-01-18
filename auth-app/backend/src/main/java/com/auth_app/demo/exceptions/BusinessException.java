package com.auth_app.demo.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Custom business exception for domain-specific errors.
 * Carries an error code and HTTP status for proper API responses.
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;
    private final Object[] args;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.args = null;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.args = null;
    }

    public BusinessException(ErrorCode errorCode, HttpStatus httpStatus) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.args = null;
    }

    public BusinessException(ErrorCode errorCode, HttpStatus httpStatus, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.args = null;
    }

    public BusinessException(ErrorCode errorCode, HttpStatus httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.args = null;
    }

    public BusinessException(ErrorCode errorCode, HttpStatus httpStatus, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.args = args;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Object[] getArgs() {
        return args;
    }
}
