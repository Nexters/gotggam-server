package com.nexters.death.global.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexters.death.global.exception.ErrorResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        int status,
        @Nullable T data,
        @Nullable ErrorResponse error,
        LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> success(@Nullable T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), data, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> fail(int status, ErrorResponse errorResponse) {
        return new ApiResponse<>(status, null, errorResponse, LocalDateTime.now());
    }
}
