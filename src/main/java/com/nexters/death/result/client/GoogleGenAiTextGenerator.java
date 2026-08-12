package com.nexters.death.result.client;

import com.google.genai.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "gemini", name = "enabled", havingValue = "true")
class GoogleGenAiTextGenerator implements GeminiTextGenerator {

    private final Client client;
    private final GeminiProperties properties;

    GoogleGenAiTextGenerator(Client client, GeminiProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    @Override
    public String generate(String prompt) {
        log.debug("Gemini 호출(모델: {}):\n{}", properties.model(), prompt);
        return client.models.generateContent(properties.model(), prompt, null).text();
    }
}
