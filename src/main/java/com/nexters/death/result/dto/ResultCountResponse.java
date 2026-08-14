package com.nexters.death.result.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResultCountResponse(
    @Schema(description = "총 참여자 수", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    long totalParticipants
) {
}
