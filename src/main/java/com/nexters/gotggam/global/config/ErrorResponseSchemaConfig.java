package com.nexters.gotggam.global.config;

import com.nexters.gotggam.global.exception.ErrorResponse;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrorResponseSchemaConfig {

    @Bean
    public GlobalOpenApiCustomizer errorResponseSchemaCustomizer() {
        return openApi -> {
            ResolvedSchema resolvedSchema = ModelConverters.getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(ErrorResponse.class));

            openApi.getComponents().addSchemas(resolvedSchema.schema.getName(), resolvedSchema.schema);
            resolvedSchema.referencedSchemas.forEach(openApi.getComponents()::addSchemas);
        };
    }
}
