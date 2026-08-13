package com.nexters.death.question.controller;

import com.nexters.death.global.payload.ApiResponse;
import com.nexters.death.question.dto.ActQuestionsResponse;
import com.nexters.death.question.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Question API", description = "질문지 API")
@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "질문지 구성 조회", description = "act별로 그룹핑된 활성 문항과 선택지 구성을 반환한다.")
    @GetMapping
    public ApiResponse<List<ActQuestionsResponse>> getQuestionnaire() {
        List<ActQuestionsResponse> responses = questionService.getQuestionnaire();
        return ApiResponse.success(responses);
    }
}
