package com.auth_app.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for error handling.
 */
@Configuration
@ConfigurationProperties(prefix = "app.errors")
public class ErrorProperties {

    /**
     * Whether to include stack traces in error responses.
     * Should be false in production.
     */
    private boolean includeStacktrace = false;

    /**
     * Whether to use RFC 7807 Problem Details format instead of compact ApiError.
     */
    private boolean useProblemDetails = false;

    /**
     * Base URI for problem types (RFC 7807).
     */
    private String problemTypeBaseUri = "https://api.example.com/problems";

    public boolean isIncludeStacktrace() {
        return includeStacktrace;
    }

    public void setIncludeStacktrace(boolean includeStacktrace) {
        this.includeStacktrace = includeStacktrace;
    }

    public boolean isUseProblemDetails() {
        return useProblemDetails;
    }

    public void setUseProblemDetails(boolean useProblemDetails) {
        this.useProblemDetails = useProblemDetails;
    }

    public String getProblemTypeBaseUri() {
        return problemTypeBaseUri;
    }

    public void setProblemTypeBaseUri(String problemTypeBaseUri) {
        this.problemTypeBaseUri = problemTypeBaseUri;
    }
}
