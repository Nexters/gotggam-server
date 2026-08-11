package com.nexters.death.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements BaseError {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_001", "서버 내부 오류입니다."),
    NOT_FOUND_END_POINT(HttpStatus.NOT_FOUND, "COMMON_002", "존재하지 않는 API입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_003", "지원하지 않는 HTTP 메서드입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_004", "잘못된 입력값입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "COMMON_005", "요청 타입이 올바르지 않습니다."),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON_006", "필수 요청 파라미터가 누락되었습니다."),
    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "COMMON_007", "요청 본문 형식이 올바르지 않습니다."),
    FILE_TOO_LARGE(HttpStatus.CONTENT_TOO_LARGE, "COMMON_008", "업로드 용량이 허용 범위를 초과했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_009", "잘못된 요청입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
