package com.nexters.death.global.config;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;

@Configuration
public class JpaAuditingConfig {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now(KST));
    }

}
