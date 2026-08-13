package com.nexters.death.result.dto;

import com.nexters.death.result.entity.ResultCharacter;
import io.swagger.v3.oas.annotations.media.Schema;

public record CharacterResponse(

    @Schema(description = "얼굴형 타입", example = "1")
    Short faceType,

    @Schema(description = "머리 타입", example = "1")
    Short hairType,

    @Schema(description = "눈 타입", example = "1")
    Short eyeType,

    @Schema(description = "코 타입", example = "1")
    Short noseType,

    @Schema(description = "입 타입", example = "1")
    Short mouthType
) {

    public static CharacterResponse from(ResultCharacter character) {
        return new CharacterResponse(
            character.getFaceType(),
            character.getHairType(),
            character.getEyeType(),
            character.getNoseType(),
            character.getMouthType()
        );
    }
}
