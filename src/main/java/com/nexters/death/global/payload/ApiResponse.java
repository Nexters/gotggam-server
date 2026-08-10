package com.nexters.death.global.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexters.death.global.exception.ErrorResponse;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        @Nullable T data,
        @Nullable ErrorResponse error,
        LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> success(@Nullable T data) {
        return new ApiResponse<>(data, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> fail(ErrorResponse errorResponse) {
        return new ApiResponse<>(null, errorResponse, LocalDateTime.now());
    }
}
