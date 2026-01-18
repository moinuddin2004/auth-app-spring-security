package com.auth_app.demo.common;

import com.auth_app.demo.config.RequestIdFilter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Production-grade API response wrapper.
 * Provides consistent response structure across all API endpoints.
 *
 * @param <T> The type of data being returned
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private int status;
    private String message;
    private T data;
    private Instant timestamp;
    private String requestId;
    private PageInfo pagination;

    public ApiResponse() {
        this.timestamp = Instant.now();
        this.requestId = RequestIdFilter.getCurrentRequestId();
    }

    private ApiResponse(Builder<T> builder) {
        this.success = builder.success;
        this.status = builder.status;
        this.message = builder.message;
        this.data = builder.data;
        this.timestamp = Instant.now();
        this.requestId = RequestIdFilter.getCurrentRequestId();
        this.pagination = builder.pagination;
    }

    // ========== Static Factory Methods ==========

    /**
     * Create a success response with data.
     */
    public static <T> ApiResponse<T> success(T data) {
        return new Builder<T>()
                .success(true)
                .status(200)
                .message("Success")
                .data(data)
                .build();
    }

    /**
     * Create a success response with data and custom message.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new Builder<T>()
                .success(true)
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Create a success response with data and custom status.
     */
    public static <T> ApiResponse<T> success(T data, int status, String message) {
        return new Builder<T>()
                .success(true)
                .status(status)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Create a success response for resource creation (201).
     */
    public static <T> ApiResponse<T> created(T data) {
        return new Builder<T>()
                .success(true)
                .status(201)
                .message("Created successfully")
                .data(data)
                .build();
    }

    /**
     * Create a success response for resource creation with custom message.
     */
    public static <T> ApiResponse<T> created(T data, String message) {
        return new Builder<T>()
                .success(true)
                .status(201)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Create a success response with no content (204).
     */
    public static <T> ApiResponse<T> noContent() {
        return new Builder<T>()
                .success(true)
                .status(204)
                .message("No content")
                .build();
    }

    /**
     * Create a success response with pagination info.
     */
    public static <T> ApiResponse<T> successWithPagination(T data, PageInfo pagination) {
        return new Builder<T>()
                .success(true)
                .status(200)
                .message("Success")
                .data(data)
                .pagination(pagination)
                .build();
    }

    /**
     * Create an error response.
     */
    public static <T> ApiResponse<T> error(int status, String message) {
        return new Builder<T>()
                .success(false)
                .status(status)
                .message(message)
                .build();
    }

    // ========== Getters and Setters ==========

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public PageInfo getPagination() {
        return pagination;
    }

    public void setPagination(PageInfo pagination) {
        this.pagination = pagination;
    }

    // ========== Builder ==========

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private boolean success;
        private int status;
        private String message;
        private T data;
        private PageInfo pagination;

        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder<T> status(int status) {
            this.status = status;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> pagination(PageInfo pagination) {
            this.pagination = pagination;
            return this;
        }

        public ApiResponse<T> build() {
            return new ApiResponse<>(this);
        }
    }

    // ========== Pagination Info ==========

    /**
     * Pagination metadata for list responses.
     */
    public static class PageInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;

        public PageInfo() {}

        public PageInfo(int page, int size, long totalElements, int totalPages) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.hasNext = page < totalPages - 1;
            this.hasPrevious = page > 0;
        }

        // Getters and Setters

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }

        public boolean isHasPrevious() {
            return hasPrevious;
        }

        public void setHasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
        }
    }
}
