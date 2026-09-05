package com.nexters.gotggam.result.dto;

import com.nexters.gotggam.result.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SurveyResultResponse(

    @Schema(description = "결과 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long resultId,

    @Schema(description = "결과 공유 토큰", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    UUID shareToken,

    @Schema(description = "이름", example = "김철수", requiredMode = Schema.RequiredMode.REQUIRED)
    String name,

    @Schema(description = "생년월일", example = "1995-03-15", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate birthDate,

    @Schema(description = "성별", example = "MALE", requiredMode = Schema.RequiredMode.REQUIRED)
    Gender gender,

    @Schema(description = "예상 사망 나이", example = "72", requiredMode = Schema.RequiredMode.REQUIRED)
    int expectedLife,

    @Schema(description = "오늘의 한마디", example = "오늘도 무사한 하루 되세요", requiredMode = Schema.RequiredMode.REQUIRED)
    String todayMessage,

    @Schema(description = "경고 메시지", requiredMode = Schema.RequiredMode.REQUIRED)
    String warningMessage,

    @Schema(description = "캐릭터 얼굴", requiredMode = Schema.RequiredMode.REQUIRED)
    CharacterResponse character,

    @Schema(description = "카테고리별 차감된 수명", requiredMode = Schema.RequiredMode.REQUIRED)
    List<CategoryPenaltyResponse> categoryPenalties,

    @Schema(description = "특별 준수 사항 (1~3개)", requiredMode = Schema.RequiredMode.REQUIRED)
    List<String> specialRules
) {
}
