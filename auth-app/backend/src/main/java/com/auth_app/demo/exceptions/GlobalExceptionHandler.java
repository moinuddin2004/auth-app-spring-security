package com.auth_app.demo.exceptions;

import com.auth_app.demo.config.ErrorProperties;
import com.auth_app.demo.config.RequestIdFilter;
import com.auth_app.demo.exceptions.model.ApiError;
import com.auth_app.demo.exceptions.model.ProblemDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * Provides consistent error responses for all exception types.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ErrorProperties errorProperties;

    public GlobalExceptionHandler(ErrorProperties errorProperties) {
        this.errorProperties = errorProperties;
    }

    // ========== Business Exceptions ==========

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        String requestId = RequestIdFilter.getCurrentRequestId();
        HttpStatus status = ex.getHttpStatus();

        log.warn("Business exception [{}] - Code: {}, Message: {}, RequestId: {}",
                ex.getClass().getSimpleName(),
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                requestId);

        return buildErrorResponse(
                status,
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                request.getRequestURI(),
                requestId,
                null,
                ex
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        String requestId = RequestIdFilter.getCurrentRequestId();

        log.info("Entity not found - Message: {}, RequestId: {}", ex.getMessage(), requestId);

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                request.getRequestURI(),
                requestId,
                null,
                ex
        );
    }

    // ========== Validation Exceptions ==========

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String requestId = RequestIdFilter.getCurrentRequestId();
        String path = extractPath(request);

        List<ApiError.FieldError> fieldErrors = extractFieldErrors(ex.getBindingResult());

        log.warn("Validation failed - {} field errors, RequestId: {}", fieldErrors.size(), requestId);

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_FAILED.getCode(),
                "Validation failed for one or more fields",
                path,
                requestId,
                fieldErrors,
                ex
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String requestId = RequestIdFilter.getCurrentRequestId();

        List<ApiError.FieldError> fieldErrors = ex.getConstraintViolations().stream()
                .map(this::mapConstraintViolation)
                .collect(Collectors.toList());

        log.warn("Constraint violation - {} violations, RequestId: {}", fieldErrors.size(), requestId);

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_FAILED.getCode(),
                "Constraint validation failed",
                request.getRequestURI(),
                requestId,
                fieldErrors,
                ex
        );
    }

    // ========== Bad Request Exceptions ==========

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String requestId = RequestIdFilter.getCurrentRequestId();
        String path = extractPath(request);

        log.warn("Malformed JSON request - RequestId: {}, Error: {}", requestId, ex.getMessage());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.BAD_REQUEST.getCode(),
                "Malformed JSON request body",
                path,
                requestId,
                null,
                ex
        );
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String requestId = RequestIdFilter.getCurrentRequestId();
        String path = extractPath(request);

        log.warn("Missing request parameter: {} - RequestId: {}", ex.getParameterName(), requestId);

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.BAD_REQUEST.getCode(),
                String.format("Missing required parameter: %s", ex.getParameterName()),
                path,
                requestId,
                null,
                ex
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String requestId = RequestIdFilter.getCurrentRequestId();

        String message = String.format("Parameter '%s' should be of type %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        log.warn("Type mismatch - {}, RequestId: {}", message, requestId);

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.BAD_REQUEST.getCode(),
                message,
                request.getRequestURI(),
                requestId,
                null,
                ex
        );
    }

    // ========== Security Exceptions ==========

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        String requestId = RequestIdFilter.getCurrentRequestId();

        log.warn("Access denied - Path: {}, RequestId: {}", request.getRequestURI(), requestId);

        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                ErrorCode.AUTH_ACCESS_DENIED.getCode(),
                "You do not have permission to access this resource",
                request.getRequestURI(),
                requestId,
                null,
                ex
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        String requestId = RequestIdFilter.getCurrentRequestId();

        log.warn("Authentication failed - Path: {}, RequestId: {}, Error: {}",
                request.getRequestURI(), requestId, ex.getMessage());

        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ErrorCode.AUTH_INVALID_CREDENTIALS.getCode(),
                "Authentication failed",
                request.getRequestURI(),
                requestId,
                null,
                ex
        );
    }

    // ========== Generic Exception Handler ==========

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtExceptions(Exception ex, HttpServletRequest request) {
        String requestId = RequestIdFilter.getCurrentRequestId();

        log.error("Unhandled exception - RequestId: {}", requestId, ex);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_ERROR.getCode(),
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI(),
                requestId,
                null,
                ex
        );
    }

    // ========== Helper Methods ==========

    private ResponseEntity<Object> buildErrorResponse(
            HttpStatus status,
            String code,
            String message,
            String path,
            String requestId,
            List<ApiError.FieldError> fieldErrors,
            Exception ex) {

        if (errorProperties.isUseProblemDetails()) {
            return buildProblemDetailsResponse(status, code, message, path, requestId, fieldErrors, ex);
        }
        return buildApiErrorResponse(status, code, message, path, requestId, fieldErrors, ex);
    }

    private ResponseEntity<Object> buildApiErrorResponse(
            HttpStatus status,
            String code,
            String message,
            String path,
            String requestId,
            List<ApiError.FieldError> fieldErrors,
            Exception ex) {

        ApiError.Builder builder = ApiError.builder()
                .status(status.value())
                .code(code)
                .message(message)
                .path(path)
                .requestId(requestId);

        if (fieldErrors != null && !fieldErrors.isEmpty()) {
            builder.errors(fieldErrors);
        }

        if (errorProperties.isIncludeStacktrace()) {
            builder.trace(getStackTraceAsString(ex));
        }

        return ResponseEntity.status(status).body(builder.build());
    }

    private ResponseEntity<Object> buildProblemDetailsResponse(
            HttpStatus status,
            String code,
            String message,
            String path,
            String requestId,
            List<ApiError.FieldError> fieldErrors,
            Exception ex) {

        ProblemDetails.Builder builder = ProblemDetails.builder()
                .type(URI.create(errorProperties.getProblemTypeBaseUri() + "/" + code.toLowerCase().replace("_", "-")))
                .title(status.getReasonPhrase())
                .status(status.value())
                .detail(message)
                .instance(path)
                .code(code)
                .requestId(requestId);

        if (fieldErrors != null && !fieldErrors.isEmpty()) {
            builder.errors(fieldErrors);
        }

        return ResponseEntity.status(status).body(builder.build());
    }

    private List<ApiError.FieldError> extractFieldErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(error -> new ApiError.FieldError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()
                ))
                .collect(Collectors.toList());
    }

    private ApiError.FieldError mapConstraintViolation(ConstraintViolation<?> violation) {
        String field = violation.getPropertyPath().toString();
        // Extract just the field name from the path
        if (field.contains(".")) {
            field = field.substring(field.lastIndexOf('.') + 1);
        }
        return new ApiError.FieldError(
                field,
                violation.getMessage(),
                violation.getInvalidValue()
        );
    }

    private String extractPath(WebRequest request) {
        String description = request.getDescription(false);
        if (description.startsWith("uri=")) {
            return description.substring(4);
        }
        return description;
    }

    private String getStackTraceAsString(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
}
