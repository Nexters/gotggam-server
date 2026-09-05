package com.nexters.gotggam.result.exception;

import com.nexters.gotggam.global.exception.error.BaseError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResultErrorCode implements BaseError {

    INVALID_ANSWER(HttpStatus.BAD_REQUEST, "RESULT_001", "유효하지 않은 설문 답변입니다."),
    INCOMPLETE_SURVEY(HttpStatus.BAD_REQUEST, "RESULT_002", "모든 문항에 답변해야 합니다."),
    UNKNOWN_CATEGORY_PILLAR(HttpStatus.INTERNAL_SERVER_ERROR, "RESULT_003", "알 수 없는 카테고리입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
