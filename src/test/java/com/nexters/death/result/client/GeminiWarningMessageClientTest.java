package com.nexters.death.result.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GeminiWarningMessageClientTest {

    private static final String FALLBACK_MESSAGE = "이대로면 오래 못 산다냥.";

    @Mock
    private GeminiTextGenerator generator;

    @InjectMocks
    private GeminiWarningMessageClient client;

    private WarningMessageRequest request() {
        return new WarningMessageRequest(
            new BigDecimal("5.00"),
            new BigDecimal("3.00"),
            new BigDecimal("2.00")
        );
    }

    @Test
    @DisplayName("생성된 문구를 앞뒤 공백만 제거해 그대로 반환한다")
    void returnsGeneratedText() {
        given(generator.generate(anyString()))
            .willReturn("  게으름이 네 수명을 갉아먹고 있다냥  ");

        String result = client.generateWarningMessage(request());

        assertThat(result).isEqualTo("게으름이 네 수명을 갉아먹고 있다냥");
    }

    @Test
    @DisplayName("Gemini 호출이 예외를 던지면 기본 문구로 폴백한다")
    void fallsBackOnException() {
        given(generator.generate(anyString()))
            .willThrow(new RuntimeException("Gemini API 오류"));

        String result = client.generateWarningMessage(request());

        assertThat(result).isEqualTo(FALLBACK_MESSAGE);
    }

    @Test
    @DisplayName("빈 문자열이 생성되면 기본 문구로 폴백한다")
    void fallsBackOnBlank() {
        given(generator.generate(anyString()))
            .willReturn("   ");

        String result = client.generateWarningMessage(request());

        assertThat(result).isEqualTo(FALLBACK_MESSAGE);
    }

    @Test
    @DisplayName("25자를 초과하면 중간에 자르지 않고 기본 문구로 폴백한다")
    void fallsBackWhenTooLong() {
        String tooLong = "게으름이 네 수명을 야금야금 갉아먹고 있으니 조심하라냥";
        given(generator.generate(anyString()))
            .willReturn(tooLong);

        String result = client.generateWarningMessage(request());

        assertThat(result).isEqualTo(FALLBACK_MESSAGE);
    }
}
