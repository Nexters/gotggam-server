package com.nexters.gotggam.global.exception;

import com.nexters.gotggam.global.exception.error.BaseError;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Getter
public class BusinessException extends RuntimeException {

    private final BaseError baseError;
    @Nullable
    private final List<ErrorResponse.FieldError> fieldErrors;

    public BusinessException(BaseError baseError) {
        this(baseError, null);
    }

    public BusinessException(BaseError baseError, @Nullable List<ErrorResponse.FieldError> fieldErrors) {
        super(baseError.getMessage());
        this.baseError = baseError;
        this.fieldErrors = fieldErrors;
    }
}
