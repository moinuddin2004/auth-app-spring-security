package com.auth_app.demo.exceptions.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * RFC 7807 Problem Details response model.
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7807">RFC 7807</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDetails {

    private URI type;
    private String title;
    private int status;
    private String detail;
    private URI instance;
    
    // Extensions
    private Instant timestamp;
    private String code;
    private String requestId;
    private List<ApiError.FieldError> errors;
    private Map<String, Object> extensions;

    public ProblemDetails() {
        this.timestamp = Instant.now();
    }

    // Getters and Setters

    public URI getType() {
        return type;
    }

    public void setType(URI type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public URI getInstance() {
        return instance;
    }

    public void setInstance(URI instance) {
        this.instance = instance;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<ApiError.FieldError> getErrors() {
        return errors;
    }

    public void setErrors(List<ApiError.FieldError> errors) {
        this.errors = errors;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ProblemDetails problemDetails = new ProblemDetails();

        public Builder type(URI type) {
            problemDetails.setType(type);
            return this;
        }

        public Builder type(String type) {
            problemDetails.setType(URI.create(type));
            return this;
        }

        public Builder title(String title) {
            problemDetails.setTitle(title);
            return this;
        }

        public Builder status(int status) {
            problemDetails.setStatus(status);
            return this;
        }

        public Builder detail(String detail) {
            problemDetails.setDetail(detail);
            return this;
        }

        public Builder instance(URI instance) {
            problemDetails.setInstance(instance);
            return this;
        }

        public Builder instance(String instance) {
            problemDetails.setInstance(URI.create(instance));
            return this;
        }

        public Builder code(String code) {
            problemDetails.setCode(code);
            return this;
        }

        public Builder requestId(String requestId) {
            problemDetails.setRequestId(requestId);
            return this;
        }

        public Builder errors(List<ApiError.FieldError> errors) {
            problemDetails.setErrors(errors);
            return this;
        }

        public Builder extensions(Map<String, Object> extensions) {
            problemDetails.setExtensions(extensions);
            return this;
        }

        public ProblemDetails build() {
            return problemDetails;
        }
    }
}
