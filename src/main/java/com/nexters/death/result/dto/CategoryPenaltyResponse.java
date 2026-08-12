package com.nexters.death.result.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryPenaltyResponse(

    @Schema(description = "카테고리 ID", example = "1")
    Long categoryId,

    @Schema(description = "카테고리 이름", example = "몸")
    String categoryName,

    @Schema(description = "해당 카테고리에서 차감된 수명", example = "3")
    int penalty
) {
}
