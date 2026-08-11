package com.nexters.death.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
            .title("곧감 API")
            .description("곧감 서비스 API 명세서")
            .version("v0.0.1");

        return new OpenAPI()
            .info(info);
    }
}
