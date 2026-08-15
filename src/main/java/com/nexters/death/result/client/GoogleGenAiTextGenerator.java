package com.nexters.death.result.client;

import com.google.genai.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "gemini", name = "enabled", havingValue = "true")
class GoogleGenAiTextGenerator implements GeminiTextGenerator {

    private final Client client;
    private final GeminiProperties properties;

    @Override
    public String generate(String prompt) {
        // 프롬프트에는 사용자 이름이 포함되므로 원문은 남기지 않고 모델명과 길이만 기록한다.
        log.debug("Gemini 호출(모델: {}, 프롬프트 {}자)", properties.model(), prompt.length());
        return client.models.generateContent(properties.model(), prompt, null).text();
    }
}
