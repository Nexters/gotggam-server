package com.nexters.gotggam.result.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryPenaltyResponse(

    @Schema(description = "카테고리 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long categoryId,

    @Schema(description = "카테고리 이름", example = "몸", requiredMode = Schema.RequiredMode.REQUIRED)
    String categoryName,

    @Schema(description = "해당 카테고리에서 차감된 수명", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    int penalty
) {
}
