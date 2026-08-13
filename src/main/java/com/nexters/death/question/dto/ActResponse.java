package com.nexters.death.question.dto;

import com.nexters.death.question.entity.Act;
import io.swagger.v3.oas.annotations.media.Schema;

public record ActResponse(

    @Schema(description = "act 코드", example = "morning")
    String code,

    @Schema(description = "act 이름", example = "아침")
    String label
) {

    public static ActResponse from(Act act) {
        return new ActResponse(act.getCode(), act.getLabel());
    }
}
