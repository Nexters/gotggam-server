package com.nexters.death.result.client;

import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "gemini", name = "enabled", havingValue = "true")
class GeminiWarningMessageClient implements WarningMessageClient {

    private static final int MAX_LENGTH = 25;
    private static final String FALLBACK_MESSAGE = "이대로면 오래 못 산다냥.";

    // 모델이 프롬프트 지시를 어기고 "문구(9자)"처럼 글자수를 덧붙이는 경우가 있어, 후행 표기를 제거한다.
    private static final Pattern TRAILING_CHAR_COUNT = Pattern.compile("\\s*[(（]\\s*\\d+\\s*자\\s*[)）]\\s*$");
    private static final String PROMPT_TEMPLATE = """
        너는 사람의 생활 습관을 지켜보며 남은 수명을 경고하는 저승 고양이다.
        아래 정보에서, 남은 수명을 가장 많이 깎은 영역을 근거로 한 문장짜리 경고를 만들어라.

        몸(신체 습관) 페널티: %s
        마음(정신 습관) 페널티: %s
        태도(생활 태도) 페널티: %s

        규칙:
        - 반드시 한국어 한 문장.
        - 공백을 포함해 25자 이내. 이 길이 제한은 반드시 지킨다.
        - 섬뜩하고 극적인 어조로 죽음과 줄어드는 수명을 암시하되, 지나치게 잔혹하거나 혐오스럽지는 않게.
        - 고양이 말투로, 문장 끝을 "냥"으로 맺는다. (예: "잠은 선택이 아니라 생존이다냥")
        - 따옴표, 부연 설명, 머리말 없이 경고 문구 자체만 출력.
        - "(9자)"처럼 글자수나 길이를 함께 적지 않는다. 경고 문구 외에는 아무것도 붙이지 않는다.
        """;

    private final GeminiTextGenerator generator;

    @Override
    public String generateWarningMessage(WarningMessageRequest request) {
        try {
            String generated = generator.generate(buildPrompt(request));
            int length = generated == null ? 0 : characterCount(generated.strip());
            log.debug("Gemini 응답 원문({}자): {}", length, generated);
            return normalize(generated);
        } catch (Exception e) {
            log.warn("Gemini 경고 메시지 생성 실패, 기본 문구로 대체", e);
            return FALLBACK_MESSAGE;
        }
    }

    private String buildPrompt(WarningMessageRequest request) {
        return PROMPT_TEMPLATE.formatted(
            request.bodyPenalty(),
            request.mindPenalty(),
            request.attitudePenalty()
        );
    }

    // 모델이 빈 값이나 25자를 넘는 문구를 반환하면, 중간에 잘라 "냥"을 잃는 대신 기본 문구로 대체한다.
    private String normalize(String generated) {
        if (generated == null || generated.isBlank()) {
            return FALLBACK_MESSAGE;
        }
        String trimmed = stripTrailingCharCount(generated.strip());
        if (trimmed.isBlank()) {
            return FALLBACK_MESSAGE;
        }
        int length = characterCount(trimmed);
        if (length > MAX_LENGTH) {
            log.warn("Gemini 경고 메시지가 {}자를 초과({}자)해 기본 문구로 대체: {}", MAX_LENGTH, length, trimmed);
            return FALLBACK_MESSAGE;
        }
        return trimmed;
    }

    private String stripTrailingCharCount(String text) {
        return TRAILING_CHAR_COUNT.matcher(text).replaceAll("").strip();
    }

    // 이모지 등 비-BMP 문자를 2로 세지 않도록 코드포인트 기준으로 글자수를 센다.
    private int characterCount(String text) {
        return text.codePointCount(0, text.length());
    }
}
