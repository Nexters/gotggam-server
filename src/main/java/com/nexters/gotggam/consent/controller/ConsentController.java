package com.nexters.gotggam.consent.controller;

import com.nexters.gotggam.consent.dto.ConsentDocumentResponse;
import com.nexters.gotggam.consent.service.ConsentService;
import com.nexters.gotggam.global.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Consent API", description = "약관 API")
@RestController
@RequestMapping("/api/v1/consent-documents")
@RequiredArgsConstructor
public class ConsentController {

    private final ConsentService consentService;

    @Operation(summary = "최신 약관 조회", description = "약관 유형별 최신 버전의 본문(마크다운)과 버전을 한 건씩 반환한다.")
    @GetMapping
    public ApiResponse<List<ConsentDocumentResponse>> getLatestDocuments() {
        List<ConsentDocumentResponse> responses = consentService.getLatestDocuments();
        return ApiResponse.success(responses);
    }
}
