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
        log.debug("Gemini 호출(모델: {}):\n{}", properties.model(), prompt);
        return client.models.generateContent(properties.model(), prompt, null).text();
    }
}
