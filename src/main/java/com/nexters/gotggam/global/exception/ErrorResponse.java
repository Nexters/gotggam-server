package com.nexters.gotggam.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexters.gotggam.global.exception.error.BaseError;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String code,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String message,
        @Nullable List<FieldError> fieldErrors
) {
    public record FieldError(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String field,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String reason
    ) {
    }

    public static ErrorResponse of(BaseError baseError, @Nullable List<FieldError> fieldErrors) {
        return new ErrorResponse(baseError.getCode(), baseError.getMessage(), fieldErrors);
    }
}
