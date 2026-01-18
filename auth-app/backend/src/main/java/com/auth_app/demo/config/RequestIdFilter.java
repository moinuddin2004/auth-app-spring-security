package com.auth_app.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to handle Request ID / Correlation ID for distributed tracing.
 * - Reads X-Request-ID from inbound request if present, otherwise generates UUID.
 * - Puts requestId into MDC for logging.
 * - Returns requestId in response headers.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String MDC_REQUEST_ID_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        // Store in MDC for logging
        MDC.put(MDC_REQUEST_ID_KEY, requestId);
        
        // Add to response headers
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Clean up MDC to prevent memory leaks in thread pools
            MDC.remove(MDC_REQUEST_ID_KEY);
        }
    }

    /**
     * Get current request ID from MDC.
     */
    public static String getCurrentRequestId() {
        String requestId = MDC.get(MDC_REQUEST_ID_KEY);
        return requestId != null ? requestId : "N/A";
    }
}
