package com.auth_app.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for RequestIdFilter.
 */
@ExtendWith(MockitoExtension.class)
class RequestIdFilterTest {

    private RequestIdFilter requestIdFilter;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        requestIdFilter = new RequestIdFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        MDC.clear();
    }

    @Test
    @DisplayName("Should use X-Request-ID from header when provided")
    void shouldUseRequestIdFromHeader() throws ServletException, IOException {
        // Given
        String expectedRequestId = "test-request-id-123";
        request.addHeader(RequestIdFilter.REQUEST_ID_HEADER, expectedRequestId);

        // When
        requestIdFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(response.getHeader(RequestIdFilter.REQUEST_ID_HEADER)).isEqualTo(expectedRequestId);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should generate UUID when X-Request-ID header is missing")
    void shouldGenerateUuidWhenHeaderMissing() throws ServletException, IOException {
        // When
        requestIdFilter.doFilterInternal(request, response, filterChain);

        // Then
        String generatedRequestId = response.getHeader(RequestIdFilter.REQUEST_ID_HEADER);
        assertThat(generatedRequestId).isNotNull();
        assertThat(generatedRequestId).matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should generate UUID when X-Request-ID header is blank")
    void shouldGenerateUuidWhenHeaderBlank() throws ServletException, IOException {
        // Given
        request.addHeader(RequestIdFilter.REQUEST_ID_HEADER, "   ");

        // When
        requestIdFilter.doFilterInternal(request, response, filterChain);

        // Then
        String generatedRequestId = response.getHeader(RequestIdFilter.REQUEST_ID_HEADER);
        assertThat(generatedRequestId).isNotNull();
        assertThat(generatedRequestId).matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
    }

    @Test
    @DisplayName("Should clear MDC after filter chain completes")
    void shouldClearMdcAfterFilterChain() throws ServletException, IOException {
        // Given
        String requestId = "test-request-id";
        request.addHeader(RequestIdFilter.REQUEST_ID_HEADER, requestId);

        // When
        requestIdFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(MDC.get(RequestIdFilter.MDC_REQUEST_ID_KEY)).isNull();
    }

    @Test
    @DisplayName("Should set requestId in response header")
    void shouldSetRequestIdInResponseHeader() throws ServletException, IOException {
        // Given
        String requestId = "my-request-id";
        request.addHeader(RequestIdFilter.REQUEST_ID_HEADER, requestId);

        // When
        requestIdFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(response.getHeader(RequestIdFilter.REQUEST_ID_HEADER)).isEqualTo(requestId);
    }

    @Test
    @DisplayName("getCurrentRequestId should return N/A when not in request context")
    void shouldReturnNAWhenNotInRequestContext() {
        // When
        String requestId = RequestIdFilter.getCurrentRequestId();

        // Then
        assertThat(requestId).isEqualTo("N/A");
    }
}
