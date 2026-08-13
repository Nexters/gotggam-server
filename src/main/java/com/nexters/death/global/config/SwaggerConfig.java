package com.nexters.death.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerConfig {

    private final SwaggerProperties swaggerProperties;

    public SwaggerConfig(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
            .title("곧감 API")
            .description("곧감 서비스 API 명세서")
            .version("v0.0.1");

        OpenAPI openAPI = new OpenAPI()
            .info(info);

        if (swaggerProperties.serverUrl() != null) {
            openAPI.servers(List.of(new Server().url(swaggerProperties.serverUrl())));
        }

        return openAPI;
    }
}
