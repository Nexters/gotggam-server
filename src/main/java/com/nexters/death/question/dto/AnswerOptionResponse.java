package com.nexters.death.question.dto;

import com.nexters.death.question.entity.QuestionOption;
import io.swagger.v3.oas.annotations.media.Schema;

public record AnswerOptionResponse(

    @Schema(description = "선택지 ID", example = "1")
    Long id,

    @Schema(description = "선택지 내용", example = "당연. 커피 수혈은 필수다.")
    String answer,

    @Schema(description = "선택 시 피드백", example = "흥, 사람이 아니라 카페인 자판기냥? 뭐, 이해는 한다.")
    String feedback
) {

    public static AnswerOptionResponse from(QuestionOption option) {
        return new AnswerOptionResponse(option.getId(), option.getContent(), option.getFeedback());
    }
}
