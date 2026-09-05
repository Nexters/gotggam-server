package com.nexters.gotggam.result.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CharacterRequest(

    @Schema(description = "얼굴형 타입", example = "1")
    @NotNull Short faceType,

    @Schema(description = "머리 타입", example = "1")
    @NotNull Short hairType,

    @Schema(description = "눈 타입", example = "1")
    @NotNull Short eyeType,

    @Schema(description = "코 타입", example = "1")
    @NotNull Short noseType,

    @Schema(description = "입 타입", example = "1")
    @NotNull Short mouthType
) {
}
