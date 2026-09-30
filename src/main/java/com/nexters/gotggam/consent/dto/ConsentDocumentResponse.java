package com.nexters.gotggam.consent.dto;

import com.nexters.gotggam.consent.entity.ConsentDocument;
import com.nexters.gotggam.consent.entity.ConsentType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ConsentDocumentResponse(

    @Schema(description = "약관 유형", example = "PRIVACY_POLICY", requiredMode = Schema.RequiredMode.REQUIRED)
    ConsentType type,

    @Schema(
        description = "약관 버전. 결과 제출 시 consents[].version에 그대로 전달한다.",
        example = "v1.0.0",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String version,

    @Schema(description = "약관 본문 (마크다운)", example = "# 개인정보 처리방침\n...", requiredMode = Schema.RequiredMode.REQUIRED)
    String content
) {

    public static ConsentDocumentResponse from(ConsentDocument document) {
        return new ConsentDocumentResponse(document.getType(), document.getVersion(), document.getContent());
    }
}
