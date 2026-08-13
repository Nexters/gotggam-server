package com.nexters.death.result.client;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gemini")
public record GeminiProperties(
    boolean enabled,
    String apiKey,
    String model,
    Duration timeout
) {

    public GeminiProperties {
        if (model == null || model.isBlank()) {
            model = "gemini-flash-latest";
        }
        if (timeout == null) {
            timeout = Duration.ofSeconds(30);
        }
    }
}
