package com.nexters.death.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ActQuestionsResponse(

    @Schema(description = "act 정보", requiredMode = Schema.RequiredMode.REQUIRED)
    ActResponse act,

    @Schema(description = "해당 act에 속한 문항 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    List<QuestionResponse> questions
) {
}
