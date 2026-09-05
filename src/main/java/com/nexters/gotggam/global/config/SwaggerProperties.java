package com.nexters.gotggam.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.swagger")
public record SwaggerProperties(
        String serverUrl
) {
}
