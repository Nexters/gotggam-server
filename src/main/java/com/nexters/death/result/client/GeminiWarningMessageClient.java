package com.nexters.death.result.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "gemini", name = "enabled", havingValue = "true")
class GeminiWarningMessageClient implements WarningMessageClient {

    private static final int MAX_LENGTH = 25;
    private static final String FALLBACK_MESSAGE = "이대로면 오래 못 산다냥.";
    private static final String PROMPT_TEMPLATE = """
        너는 사람의 생활 습관을 지켜보며 남은 수명을 경고하는 저승 고양이다.
        아래 정보에서, 남은 수명을 가장 많이 깎은 영역을 근거로 한 문장짜리 경고를 만들어라.

        이름: %s
        몸(신체 습관) 페널티: %s
        마음(정신 습관) 페널티: %s
        태도(생활 태도) 페널티: %s

        규칙:
        - 반드시 한국어 한 문장.
        - 공백을 포함해 25자 이내. 이 길이 제한은 반드시 지킨다.
        - 섬뜩하고 극적인 어조로 죽음과 줄어드는 수명을 암시하되, 지나치게 잔혹하거나 혐오스럽지는 않게.
        - 고양이 말투로, 문장 끝을 "냥"으로 맺는다. (예: "잠은 선택이 아니라 생존이다냥")
        - 25자를 지키는 것이 우선이므로 이름은 넣지 않아도 된다.
        - 따옴표, 부연 설명, 머리말 없이 경고 문구 자체만 출력.
        """;

    private final GeminiTextGenerator generator;

    GeminiWarningMessageClient(GeminiTextGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String generateWarningMessage(WarningMessageRequest request) {
        try {
            String generated = generator.generate(buildPrompt(request));
            int length = generated == null ? 0 : generated.strip().length();
            log.debug("Gemini 응답 원문({}자): {}", length, generated);
            return normalize(generated);
        } catch (Exception e) {
            log.warn("Gemini 경고 메시지 생성 실패, 기본 문구로 대체", e);
            return FALLBACK_MESSAGE;
        }
    }

    private String buildPrompt(WarningMessageRequest request) {
        return PROMPT_TEMPLATE.formatted(
            request.name(),
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
        String trimmed = generated.strip();
        if (trimmed.length() > MAX_LENGTH) {
            log.warn("Gemini 경고 메시지가 {}자를 초과({}자)해 기본 문구로 대체: {}", MAX_LENGTH, trimmed.length(), trimmed);
            return FALLBACK_MESSAGE;
        }
        return trimmed;
    }
}
