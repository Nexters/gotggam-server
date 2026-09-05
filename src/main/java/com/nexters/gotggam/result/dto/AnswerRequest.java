package com.nexters.gotggam.result.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AnswerRequest(

    @Schema(description = "문항 ID", example = "1")
    @NotNull Long questionId,

    @Schema(description = "선택한 선택지 ID", example = "3")
    @NotNull Long optionId
) {
}
