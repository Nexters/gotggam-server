package com.nexters.gotggam.global.client.toss;

import com.nexters.gotggam.global.exception.error.BaseError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TossErrorCode implements BaseError {

    TOSS_API_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "TOSS_001", "토스 서버와 통신할 수 없습니다."),
    TOSS_API_FAILED(HttpStatus.BAD_GATEWAY, "TOSS_002", "토스 API 요청에 실패했습니다."),
    TOSS_RATE_LIMITED(HttpStatus.TOO_MANY_REQUESTS, "TOSS_003", "요청이 많습니다. 잠시 후 다시 시도해 주세요."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
