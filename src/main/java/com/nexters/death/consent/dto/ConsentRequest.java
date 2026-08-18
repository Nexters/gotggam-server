package com.nexters.death.consent.dto;

import com.nexters.death.consent.entity.ConsentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ConsentRequest(

    @Schema(description = "동의 문서 유형", example = "PRIVACY_POLICY")
    @NotNull ConsentType type,

    @Schema(description = "동의 문서 버전", example = "v1.0.0")
    @NotBlank @Size(max = 20) String version,

    @Schema(description = "동의 여부", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    boolean agreed
) {
}
