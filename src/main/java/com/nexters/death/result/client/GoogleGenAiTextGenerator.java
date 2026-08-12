package com.nexters.death.result.client;

import com.google.genai.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

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
        return client.models.generateContent(properties.model(), prompt, null).text();
    }
}
