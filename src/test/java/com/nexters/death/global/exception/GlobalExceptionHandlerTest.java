package com.nexters.death.global.exception;

import com.nexters.death.global.exception.error.GlobalErrorCode;
import com.nexters.death.global.payload.ApiResponse;
import tools.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new TestController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 성공_응답은_data에_결과를_담고_error는_없이_반환한다() throws Exception {
        mockMvc.perform(get("/test/success"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("hello"))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void 비즈니스_예외는_도메인_코드를_그대로_code에_담아_반환한다() throws Exception {
        mockMvc.perform(post("/test/business-error"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("COMMON_004"))
                .andExpect(jsonPath("$.error.message").value("잘못된 입력값입니다."));
    }

    @Test
    void 여러_필드가_유효하지_않으면_각_필드의_fieldError를_담아_실패_응답을_반환한다() throws Exception {
        TestRequest request = new TestRequest("", null);

        mockMvc.perform(post("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error.code").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.error.fieldErrors.length()").value(2))
                .andExpect(jsonPath("$.error.fieldErrors[*].field", containsInAnyOrder("name", "age")));
    }

    @Test
    void 지원하지_않는_HTTP_메서드로_요청하면_405_상태코드를_반환한다() throws Exception {
        mockMvc.perform(get("/test"))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.error.code").value("HttpRequestMethodNotSupportedException"))
                .andExpect(jsonPath("$.error.message").value("지원하지 않는 HTTP 메서드입니다."));
    }

    @Test
    void 필수_파라미터가_없으면_400_상태코드를_반환한다() throws Exception {
        mockMvc.perform(get("/test/param"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("MissingServletRequestParameterException"))
                .andExpect(jsonPath("$.error.message").value("필수 요청 파라미터가 누락되었습니다."));
    }

    @Test
    void 파라미터_타입이_맞지_않으면_400_상태코드를_반환한다() throws Exception {
        mockMvc.perform(get("/test/number").param("value", "not-a-number"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("MethodArgumentTypeMismatchException"))
                .andExpect(jsonPath("$.error.message").value("요청 타입이 올바르지 않습니다."));
    }

    @Test
    void JSON_형식이_깨져있으면_400_상태코드를_반환한다() throws Exception {
        mockMvc.perform(post("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("HttpMessageNotReadableException"))
                .andExpect(jsonPath("$.error.message").value("요청 본문 형식이 올바르지 않습니다."));
    }

    @Test
    void 지원하지_않는_미디어_타입으로_요청하면_415_상태코드와_예외_정보를_담아_실패_응답을_반환한다() throws Exception {
        mockMvc.perform(post("/test")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("plain text"))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.error.code").value("HttpMediaTypeNotSupportedException"));
    }

    @Test
    void 처리되지_않은_예외가_발생하면_500_상태코드와_예외_클래스명을_반환한다() throws Exception {
        mockMvc.perform(get("/test/unknown-error"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value("IllegalStateException"))
                .andExpect(jsonPath("$.error.message").value("서버 내부 오류입니다."));
    }

    @RestController
    static class TestController {
        @PostMapping("/test")
        public String handle(@Valid @RequestBody TestRequest request) {
            return "ok";
        }

        @GetMapping("/test/success")
        public ApiResponse<TestResponse> success() {
            return ApiResponse.success(new TestResponse("hello"));
        }

        @PostMapping("/test/business-error")
        public String businessError() {
            throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
        }

        @GetMapping("/test/param")
        public String param(@RequestParam String name) {
            return name;
        }

        @GetMapping("/test/number")
        public String number(@RequestParam Integer value) {
            return value.toString();
        }

        @GetMapping("/test/unknown-error")
        public String unknownError() {
            throw new IllegalStateException("something broke");
        }
    }

    record TestRequest(
            @NotBlank String name,
            @NotNull Integer age
    ) {
    }

    record TestResponse(String message) {
    }
}
