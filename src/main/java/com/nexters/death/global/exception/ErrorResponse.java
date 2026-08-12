package com.nexters.death.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexters.death.global.exception.error.BaseError;
import jakarta.annotation.Nullable;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String code,
        String message,
        @Nullable List<FieldError> fieldErrors
) {
    public record FieldError(String field, String reason) {
    }

    public static ErrorResponse of(BaseError baseError, @Nullable List<FieldError> fieldErrors) {
        return new ErrorResponse(baseError.getCode(), baseError.getMessage(), fieldErrors);
    }
}
