package com.nexters.gotggam.consent.exception;

import com.nexters.gotggam.global.exception.error.BaseError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ConsentErrorCode implements BaseError {

    CONSENT_REQUIRED(HttpStatus.BAD_REQUEST, "CONSENT_001", "필수 동의 항목에 동의해야 합니다."),
    INCOMPLETE_CONSENT(HttpStatus.BAD_REQUEST, "CONSENT_002", "모든 동의 항목을 제출해야 합니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
