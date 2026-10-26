package com.nexters.gotggam.toss.client;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toss.api")
public record TossApiProperties(
    boolean enabled,
    String baseUrl,
    String sslBundle,
    Duration connectTimeout,
    Duration readTimeout
) {

    public TossApiProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "https://apps-in-toss-api.toss.im";
        }
        if (sslBundle == null || sslBundle.isBlank()) {
            sslBundle = "toss";
        }
        if (connectTimeout == null) {
            connectTimeout = Duration.ofSeconds(3);
        }
        if (readTimeout == null) {
            readTimeout = Duration.ofSeconds(5);
        }
    }
}
