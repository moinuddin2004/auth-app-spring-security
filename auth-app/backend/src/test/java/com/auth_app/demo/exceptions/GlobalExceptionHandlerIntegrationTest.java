package com.auth_app.demo.exceptions;

import com.auth_app.demo.config.ErrorProperties;
import com.auth_app.demo.config.RequestIdFilter;
import com.auth_app.demo.dtos.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for GlobalExceptionHandler.
 * Tests various exception scenarios and validates error responses.
 */
@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MDC.clear();
    }

    @Nested
    @DisplayName("Validation Exception Tests")
    class ValidationExceptionTests {

        @Test
        @DisplayName("Should return 400 with field errors for invalid request body")
        void shouldReturnValidationErrors() throws Exception {
            UserDto invalidUser = new UserDto();
            invalidUser.setName(""); // Invalid: blank
            invalidUser.setEmail("invalid-email"); // Invalid: not an email

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidUser)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.code").value("VAL_001"))
                    .andExpect(jsonPath("$.requestId").isNotEmpty())
                    .andExpect(jsonPath("$.errors").isArray())
                    .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));
        }

        @Test
        @DisplayName("Should return 400 for malformed JSON")
        void shouldReturnBadRequestForMalformedJson() throws Exception {
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ invalid json }"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.code").value("SYS_002"))
                    .andExpect(jsonPath("$.message").value(containsString("Malformed JSON")));
        }
    }

    @Nested
    @DisplayName("Not Found Exception Tests")
    class NotFoundExceptionTests {

        @Test
        @DisplayName("Should return 404 for non-existent user")
        void shouldReturn404ForNonExistentUser() throws Exception {
            mockMvc.perform(get("/api/users/00000000-0000-0000-0000-000000000000"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.requestId").isNotEmpty());
        }
    }

    @Nested
    @DisplayName("Request ID Tests")
    class RequestIdTests {

        @Test
        @DisplayName("Should use provided X-Request-ID header")
        void shouldUseProvidedRequestId() throws Exception {
            String customRequestId = "custom-request-id-12345";

            mockMvc.perform(get("/api/users")
                            .header("X-Request-ID", customRequestId))
                    .andExpect(status().isOk())
                    .andExpect(header().string("X-Request-ID", customRequestId));
        }

        @Test
        @DisplayName("Should generate Request ID when not provided")
        void shouldGenerateRequestIdWhenNotProvided() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("X-Request-ID"));
        }
    }

    @Nested
    @DisplayName("Type Mismatch Tests")
    class TypeMismatchTests {

        @Test
        @DisplayName("Should return 400 for invalid UUID format")
        void shouldReturn400ForInvalidUUID() throws Exception {
            mockMvc.perform(get("/api/users/not-a-uuid"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.code").value("SYS_002"));
        }
    }
}
