package com.nexters.death.global.exception;

import com.nexters.death.global.exception.error.BaseError;
import com.nexters.death.global.exception.error.GlobalErrorCode;
import com.nexters.death.global.payload.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        BaseError baseError = e.getBaseError();
        log.warn("BusinessException occurred: {}", e.getMessage());

        return ResponseEntity.status(baseError.getHttpStatus())
                .body(ApiResponse.fail(baseError.getHttpStatus().value(), ErrorResponse.of(baseError, e.getFieldErrors())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled exception occurred", e);

        GlobalErrorCode globalErrorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(globalErrorCode.getCode(), globalErrorCode.getMessage(), null);
        return ResponseEntity.status(globalErrorCode.getHttpStatus())
                .body(ApiResponse.fail(globalErrorCode.getHttpStatus().value(), errorResponse));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorResponse.FieldError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        log.warn("Method argument not valid: {}", fieldErrors);

        return buildResponseEntity(GlobalErrorCode.INVALID_INPUT_VALUE, headers, fieldErrors);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        log.warn("Http request method not supported: {}", ex.getMessage());

        return buildResponseEntity(GlobalErrorCode.METHOD_NOT_ALLOWED, headers);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        log.warn("Missing servlet request parameter: {}", ex.getMessage());

        return buildResponseEntity(GlobalErrorCode.MISSING_REQUEST_PARAMETER, headers);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        log.warn("Type mismatch: {}", ex.getMessage());

        return buildResponseEntity(GlobalErrorCode.INVALID_TYPE_VALUE, headers);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        log.warn("Http message not readable: {}", ex.getMessage());

        return buildResponseEntity(GlobalErrorCode.INVALID_JSON_FORMAT, headers);
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        log.warn("Max upload size exceeded: {}", ex.getMessage());

        return buildResponseEntity(GlobalErrorCode.FILE_TOO_LARGE, headers);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        log.warn("No handler found: {}", ex.getMessage());

        return buildResponseEntity(GlobalErrorCode.NOT_FOUND_END_POINT, headers);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        log.warn("No resource found: {}", ex.getMessage());

        return buildResponseEntity(GlobalErrorCode.NOT_FOUND_END_POINT, headers);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request
    ) {
        log.warn("Spring MVC exception handled as {}: {}", ex.getClass().getSimpleName(), ex.getMessage());

        GlobalErrorCode globalErrorCode = statusCode.is5xxServerError()
                ? GlobalErrorCode.INTERNAL_SERVER_ERROR
                : GlobalErrorCode.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(globalErrorCode.getCode(), globalErrorCode.getMessage(), null);
        return ResponseEntity.status(statusCode)
                .headers(headers)
                .body(ApiResponse.fail(statusCode.value(), errorResponse));
    }

    private ResponseEntity<Object> buildResponseEntity(BaseError baseError, HttpHeaders headers) {
        return buildResponseEntity(baseError, headers, null);
    }

    private ResponseEntity<Object> buildResponseEntity(
            BaseError baseError,
            HttpHeaders headers,
            @Nullable List<ErrorResponse.FieldError> fieldErrors
    ) {
        ErrorResponse errorResponse = new ErrorResponse(baseError.getCode(), baseError.getMessage(), fieldErrors);
        return ResponseEntity.status(baseError.getHttpStatus())
                .headers(headers)
                .body(ApiResponse.fail(baseError.getHttpStatus().value(), errorResponse));
    }
}
