package com.nexters.gotggam.policy.exception;

import com.nexters.gotggam.global.exception.error.BaseError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PolicyErrorCode implements BaseError {

    LIFE_EXPECTANCY_POLICY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "POLICY_001", "수명 정책이 설정되지 않았습니다."),
    AGE_WEIGHT_NOT_CONFIGURED(HttpStatus.INTERNAL_SERVER_ERROR, "POLICY_002", "나이대별 가중치가 설정되지 않았습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
