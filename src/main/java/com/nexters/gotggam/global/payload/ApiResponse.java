package com.nexters.gotggam.global.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexters.gotggam.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        @Schema(example = "200", requiredMode = Schema.RequiredMode.REQUIRED) int status,
        @Nullable @Schema(requiredMode = Schema.RequiredMode.REQUIRED) T data,
        @Nullable @Schema(hidden = true, requiredMode = Schema.RequiredMode.REQUIRED) ErrorResponse error,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> success(@Nullable T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), data, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> fail(int status, ErrorResponse errorResponse) {
        return new ApiResponse<>(status, null, errorResponse, LocalDateTime.now());
    }
}
