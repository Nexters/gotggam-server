package com.nexters.death.global.payload;

import com.nexters.death.global.exception.ErrorResponse;
import com.nexters.death.global.exception.error.BaseError;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public record ApiResponse<T>(
        boolean success,
        @Nullable T data,
        LocalDateTime timestamp,
        @Nullable ErrorResponse error
) {
    public static <T> ApiResponse<T> success(@Nullable T data) {
        return new ApiResponse<>(true, data, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> fail(BaseError baseError, @Nullable List<ErrorResponse.FieldError> fieldErrors) {
        return new ApiResponse<>(false, null, LocalDateTime.now(), ErrorResponse.of(baseError, fieldErrors));
    }
}
