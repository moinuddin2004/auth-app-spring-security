# Error Handling Subsystem

A production-ready error handling subsystem for Spring Boot applications.

## Features

- **Standardized error responses** - Both compact `ApiError` and RFC 7807 `ProblemDetails` formats
- **Request correlation** - `X-Request-ID` header propagation for distributed tracing
- **Comprehensive exception handling** - Validation, security, business, and generic exceptions
- **MDC logging integration** - Request ID included in all log entries
- **Configurable stack traces** - Hide in production, show in development

## Configuration Properties

Add to your `application.yaml`:

```yaml
app:
  errors:
    # Include stack traces in error responses (default: false)
    include-stacktrace: false
    
    # Use RFC 7807 Problem Details format instead of compact ApiError (default: false)  
    use-problem-details: false
    
    # Base URI for problem types when using RFC 7807 format
    problem-type-base-uri: https://api.yourapp.com/problems
```

### Profile-specific Configuration

**Development (`application-dev.yaml`)**:
```yaml
app:
  errors:
    include-stacktrace: true
    use-problem-details: false
```

**Production (`application-prod.yaml`)**:
```yaml
app:
  errors:
    include-stacktrace: false
    use-problem-details: false
```

## Logging Pattern

Include `requestId` (and optionally `traceId` for Spring Cloud Sleuth) in logs:

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{requestId:-N/A}] [%X{traceId:-}] %-5level %logger{36} - %msg%n"
```

## Dependencies

Ensure these dependencies are in your `pom.xml`:

```xml
<dependencies>
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Security (for AccessDeniedException, AuthenticationException) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- Actuator (optional, for health checks and metrics) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

## Error Response Formats

### Compact ApiError (Default)

```json
{
  "timestamp": "2026-01-18T22:00:00.000Z",
  "status": 400,
  "code": "VAL_001",
  "message": "Validation failed for one or more fields",
  "path": "/api/users",
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "errors": [
    {
      "field": "email",
      "message": "Email must be valid",
      "rejectedValue": "invalid-email"
    }
  ]
}
```

### RFC 7807 Problem Details

```json
{
  "type": "https://api.yourapp.com/problems/val-001",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed for one or more fields",
  "instance": "/api/users",
  "timestamp": "2026-01-18T22:00:00.000Z",
  "code": "VAL_001",
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "errors": [...]
}
```

## Exception Types Handled

| Exception | HTTP Status | Error Code |
|-----------|-------------|------------|
| `MethodArgumentNotValidException` | 400 | VAL_001 |
| `ConstraintViolationException` | 400 | VAL_001 |
| `HttpMessageNotReadableException` | 400 | SYS_002 |
| `MethodArgumentTypeMismatchException` | 400 | SYS_002 |
| `BusinessException` | Varies | Custom |
| `EntityNotFoundException` | 404 | RES_001 |
| `AccessDeniedException` | 403 | AUTH_003 |
| `AuthenticationException` | 401 | AUTH_001 |
| `Exception` (generic) | 500 | SYS_001 |

## Usage Examples

### Throwing Business Exceptions

```java
// Simple usage
throw new BusinessException(ErrorCode.USER_NOT_FOUND);

// With custom message
throw new BusinessException(ErrorCode.USER_NOT_FOUND, "User with email foo@bar.com not found");

// With custom HTTP status
throw new BusinessException(ErrorCode.USER_EMAIL_EXISTS, HttpStatus.CONFLICT, "Email already registered");

// Entity not found
throw new EntityNotFoundException("User", userId);
```

### Adding Custom Error Codes

Add to `ErrorCode.java`:

```java
// Order Domain
ORDER_NOT_FOUND("ORD_001", "Order not found"),
ORDER_ALREADY_PROCESSED("ORD_002", "Order has already been processed"),
```

## File Structure

```
src/main/java/com/auth_app/demo/
├── config/
│   ├── ErrorProperties.java      # Configuration properties
│   └── RequestIdFilter.java      # Request ID/Correlation ID filter
└── exceptions/
    ├── BusinessException.java    # Custom business exception
    ├── EntityNotFoundException.java
    ├── ErrorCode.java            # Error code enum
    ├── GlobalExceptionHandler.java # @ControllerAdvice
    └── model/
        ├── ApiError.java         # Compact error response
        └── ProblemDetails.java   # RFC 7807 response
```

## Testing

Run tests with:

```bash
./mvnw test -Dtest=GlobalExceptionHandlerIntegrationTest,RequestIdFilterTest
```
