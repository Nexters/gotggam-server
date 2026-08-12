package com.nexters.death.result.controller;

import com.nexters.death.global.payload.ApiResponse;
import com.nexters.death.result.dto.SurveyResultRequest;
import com.nexters.death.result.dto.SurveyResultResponse;
import com.nexters.death.result.service.ResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "\uD83E\uDEA6 Result API", description = "설문 결과 API")
@RestController
@RequestMapping("/api/v1/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @Operation(summary = "설문 결과 제출", description = "설문 답변을 받아 결과를 계산, 저장하고 결과 페이지 정보를 반환한다.")
    @PostMapping
    public ApiResponse<SurveyResultResponse> createResult(@Valid @RequestBody SurveyResultRequest request) {
        return ApiResponse.success(resultService.createResult(request));
    }
}
