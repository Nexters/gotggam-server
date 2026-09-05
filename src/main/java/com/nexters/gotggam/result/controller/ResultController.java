package com.nexters.gotggam.result.controller;

import com.nexters.gotggam.global.payload.ApiResponse;
import com.nexters.gotggam.result.dto.ResultCountResponse;
import com.nexters.gotggam.result.dto.SurveyResultRequest;
import com.nexters.gotggam.result.dto.SurveyResultResponse;
import com.nexters.gotggam.result.service.ResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Result API", description = "설문 결과 API")
@RestController
@RequestMapping("/api/v1/results")
@RequiredArgsConstructor
public class ResultController {

    // 문항 10개, 문항당 이지선다(선택지 2개) 기준 예시. 문항 N의 선택지 ID는 2N-1, 2N.
    private static final String CREATE_REQUEST_EXAMPLE = """
        {
          "name": "김철수",
          "birthDate": "1995-03-15",
          "gender": "MALE",
          "todayMessage": "오늘도 무사한 하루 되세요",
          "answers": [
            { "questionId": 1, "optionId": 2 },
            { "questionId": 2, "optionId": 3 },
            { "questionId": 3, "optionId": 6 },
            { "questionId": 4, "optionId": 7 },
            { "questionId": 5, "optionId": 10 },
            { "questionId": 6, "optionId": 11 },
            { "questionId": 7, "optionId": 14 },
            { "questionId": 8, "optionId": 15 },
            { "questionId": 9, "optionId": 18 },
            { "questionId": 10, "optionId": 19 }
          ],
          "character": {
            "faceType": 1,
            "hairType": 2,
            "eyeType": 1,
            "noseType": 3,
            "mouthType": 2
          },
          "consents": [
            { "type": "PRIVACY_POLICY", "version": "1.0", "agreed": true },
            { "type": "TERMS_OF_SERVICE", "version": "1.0", "agreed": true }
          ]
        }
        """;

    private final ResultService resultService;

    @Operation(summary = "설문 결과 제출", description = "설문 답변을 받아 결과를 계산, 저장하고 결과 페이지 정보를 반환한다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(examples = @ExampleObject(name = "10문항 이지선다 예시", value = CREATE_REQUEST_EXAMPLE))
    )
    @PostMapping
    public ApiResponse<SurveyResultResponse> createResult(@Valid @RequestBody SurveyResultRequest request) {
        SurveyResultResponse response = resultService.createResult(request);
        return ApiResponse.success(response);
    }

    @Operation(summary = "총 참여자 수 조회", description = "총 참여자 수(지금까지 설문에 참여해 생성된 결과 수)를 반환합니다.")
    @GetMapping("/count")
    public ApiResponse<ResultCountResponse> getParticipantCount() {
        ResultCountResponse response = resultService.countParticipants();
        return ApiResponse.success(response);
    }
}
