package com.nexters.gotggam.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiResponseSchemaConfig {

    private static final String API_RESPONSE_SCHEMA_NAME = "ApiResponse";
    private static final String API_RESPONSE_SCHEMA_PREFIX = "ApiResponse";

    @Bean
    public GlobalOpenApiCustomizer apiResponseSchemaCustomizer() {
        return openApi -> {
            Components components = openApi.getComponents();
            Map<String, Schema> schemas = components.getSchemas();
            if (schemas == null || openApi.getPaths() == null) {
                return;
            }

            registerBaseApiResponseSchema(components);

            List<String> wrapperNamesToRemove = new ArrayList<>();
            openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().stream()
                    .filter(operation -> operation.getResponses() != null)
                    .flatMap(operation -> operation.getResponses().values().stream())
                    .map(io.swagger.v3.oas.models.responses.ApiResponse::getContent)
                    .filter(java.util.Objects::nonNull)
                    .flatMap(content -> content.values().stream())
                    .forEach(mediaType -> {
                        String wrapperName = refName(mediaType.getSchema());
                        Schema<?> wrapperSchema = wrapperName == null ? null : schemas.get(wrapperName);
                        if (!isApiResponseWrapper(wrapperName, wrapperSchema)) {
                            return;
                        }

                        Schema<?> dataSchema = wrapperSchema.getProperties() == null
                            ? null
                            : (Schema<?>) wrapperSchema.getProperties().get("data");

                        mediaType.setSchema(buildApiResponseSchema(dataSchema));
                        wrapperNamesToRemove.add(wrapperName);
                    })
            );

            wrapperNamesToRemove.forEach(schemas::remove);
        };
    }

    private void registerBaseApiResponseSchema(Components components) {
        if (components.getSchemas() != null && components.getSchemas().containsKey(API_RESPONSE_SCHEMA_NAME)) {
            return;
        }

        Schema<Object> genericData = new Schema<>(SpecVersion.V31);
        genericData.description("실제 타입은 각 엔드포인트 응답의 data를 참고");

        components.addSchemas(API_RESPONSE_SCHEMA_NAME, buildApiResponseSchema(genericData));
    }

    private boolean isApiResponseWrapper(String schemaName, Schema<?> schema) {
        if (schemaName == null || schema == null || schema.getProperties() == null) {
            return false;
        }
        return schemaName.startsWith(API_RESPONSE_SCHEMA_PREFIX)
            && !schemaName.equals(API_RESPONSE_SCHEMA_NAME)
            && schema.getProperties().containsKey("status")
            && schema.getProperties().containsKey("timestamp");
    }

    private Schema<Object> buildApiResponseSchema(Schema<?> dataSchema) {
        Schema<Object> schema = new Schema<>(SpecVersion.V31);
        schema.addType("object");

        Schema<Object> status = new Schema<>(SpecVersion.V31);
        status.addType("integer");
        status.format("int32");
        status.example(200);
        schema.addProperty("status", status);

        schema.addProperty("data", dataSchema);

        Schema<Object> timestamp = new Schema<>(SpecVersion.V31);
        timestamp.addType("string");
        timestamp.format("date-time");
        schema.addProperty("timestamp", timestamp);

        schema.setRequired(List.of("status", "data", "timestamp"));
        return schema;
    }

    private String refName(Schema<?> schema) {
        if (schema == null || schema.get$ref() == null) {
            return null;
        }
        String ref = schema.get$ref();
        int idx = ref.lastIndexOf('/');
        return idx >= 0 ? ref.substring(idx + 1) : ref;
    }
}
