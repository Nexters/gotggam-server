package com.nexters.death.question.dto;

import com.nexters.death.question.entity.Question;
import com.nexters.death.question.entity.QuestionOption;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record QuestionResponse(

    @Schema(description = "문항 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long id,

    @Schema(description = "문항 내용", example = "아침을 깨우는 첫 선택이다냥. 아침을 거르고 커피만 마시냥?", requiredMode = Schema.RequiredMode.REQUIRED)
    String question,

    @Schema(description = "선택지 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    List<AnswerOptionResponse> answers
) {

    public static QuestionResponse from(Question question, List<QuestionOption> options) {
        return new QuestionResponse(
            question.getId(),
            question.getContent(),
            options.stream().map(AnswerOptionResponse::from).toList()
        );
    }
}
