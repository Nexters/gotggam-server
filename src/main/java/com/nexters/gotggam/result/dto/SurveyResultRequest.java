package com.nexters.gotggam.result.dto;

import com.nexters.gotggam.consent.dto.ConsentRequest;
import com.nexters.gotggam.result.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record SurveyResultRequest(

    @Schema(description = "이름", example = "김철수")
    @NotBlank @Size(max = 30) String name,

    @Schema(description = "생년월일", example = "1995-03-15")
    @NotNull @Past LocalDate birthDate,

    @Schema(description = "성별", example = "MALE")
    @NotNull Gender gender,

    @Schema(description = "오늘의 한마디 (선택, 미입력 시 기본 문장 제공)", example = "오늘도 무사한 하루 되세요")
    @Size(max = 15) String todayMessage,

    @Schema(description = "문항별 답변 목록")
    @NotEmpty List<@NotNull @Valid AnswerRequest> answers,

    @Schema(description = "캐릭터 얼굴 선택")
    @NotNull @Valid CharacterRequest character,

    @Schema(description = "동의 항목 목록")
    @NotEmpty List<@NotNull @Valid ConsentRequest> consents
) {
}
