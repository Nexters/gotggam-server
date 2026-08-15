package com.nexters.death.result.client;

import com.google.genai.Client;
import com.google.genai.types.HttpOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GeminiProperties.class)
@ConditionalOnProperty(prefix = "gemini", name = "enabled", havingValue = "true")
public class GeminiClientConfig {

    // HttpOptions.timeout()은 내부적으로 OkHttp callTimeout(밀리초)로 사용된다.
    @Bean(destroyMethod = "close")
    public Client geminiClient(GeminiProperties properties) {
        return Client.builder()
            .apiKey(properties.apiKey())
            .httpOptions(HttpOptions.builder()
                .timeout((int) properties.timeout().toMillis())
                .build())
            .build();
    }
}
