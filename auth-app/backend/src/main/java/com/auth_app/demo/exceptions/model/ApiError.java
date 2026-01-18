package com.auth_app.demo.exceptions.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Compact API error response model.
 * Used for standard error responses across the application.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private Instant timestamp;
    private int status;
    private String code;
    private String message;
    private String path;
    private String requestId;
    private List<FieldError> errors;
    private String trace;

    public ApiError() {
        this.timestamp = Instant.now();
    }

    public ApiError(int status, String code, String message, String path, String requestId) {
        this.timestamp = Instant.now();
        this.status = status;
        this.code = code;
        this.message = message;
        this.path = path;
        this.requestId = requestId;
    }

    // Getters and Setters

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldError> errors) {
        this.errors = errors;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    /**
     * Field-level validation error.
     */
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;

        public FieldError() {}

        public FieldError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }

        public void setRejectedValue(Object rejectedValue) {
            this.rejectedValue = rejectedValue;
        }
    }

    /**
     * Builder for fluent API error construction.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ApiError apiError = new ApiError();

        public Builder status(int status) {
            apiError.setStatus(status);
            return this;
        }

        public Builder code(String code) {
            apiError.setCode(code);
            return this;
        }

        public Builder message(String message) {
            apiError.setMessage(message);
            return this;
        }

        public Builder path(String path) {
            apiError.setPath(path);
            return this;
        }

        public Builder requestId(String requestId) {
            apiError.setRequestId(requestId);
            return this;
        }

        public Builder errors(List<FieldError> errors) {
            apiError.setErrors(errors);
            return this;
        }

        public Builder trace(String trace) {
            apiError.setTrace(trace);
            return this;
        }

        public ApiError build() {
            return apiError;
        }
    }
}
