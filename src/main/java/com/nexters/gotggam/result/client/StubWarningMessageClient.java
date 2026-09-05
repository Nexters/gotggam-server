package com.nexters.gotggam.result.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

// gemini.enabled=false(기본/테스트)일 때만 활성화. Gemini 구현체와 배타적으로 정확히 하나만 뜬다.
@Component
@ConditionalOnProperty(prefix = "gemini", name = "enabled", havingValue = "false", matchIfMissing = true)
public class StubWarningMessageClient implements WarningMessageClient {

    private static final String STUB_MESSAGE = "이대로면 오래 못 산다냥.";

    @Override
    public String generateWarningMessage(WarningMessageRequest request) {
        return STUB_MESSAGE;
    }
}
