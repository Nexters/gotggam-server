package com.nexters.gotggam.global.client.toss;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toss.api")
public record TossApiProperties(
    boolean enabled,
    String baseUrl,
    String sslBundle
) {

    public TossApiProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "https://apps-in-toss-api.toss.im";
        }
        if (sslBundle == null || sslBundle.isBlank()) {
            sslBundle = "toss";
        }
    }
}
