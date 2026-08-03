package com.nexters.death.global.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexters.death.global.exception.ErrorResponse;
import com.nexters.death.global.exception.error.BaseError;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        @Nullable T data,
        @Nullable ErrorResponse error,
        LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> success(@Nullable T data) {
        return new ApiResponse<>(true, data, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> fail(BaseError baseError, @Nullable List<ErrorResponse.FieldError> fieldErrors) {
        return new ApiResponse<>(false, null, ErrorResponse.of(baseError, fieldErrors), LocalDateTime.now());
    }
}
