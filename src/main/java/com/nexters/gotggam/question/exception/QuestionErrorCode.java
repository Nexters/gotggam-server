package com.nexters.gotggam.question.exception;

import com.nexters.gotggam.global.exception.error.BaseError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum QuestionErrorCode implements BaseError {

    QUESTION_OPTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "QUESTION_001", "존재하지 않는 선택지입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
