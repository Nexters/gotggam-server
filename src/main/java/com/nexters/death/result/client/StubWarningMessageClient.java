package com.nexters.death.result.client;

import org.springframework.stereotype.Component;

// Gemini 연동 전까지 고정 문구를 반환하는 스텁. 실제 연동 시 같은 인터페이스 구현체로 교체한다.
@Component
public class StubWarningMessageClient implements WarningMessageClient {

    private static final String STUB_MESSAGE = "지금처럼 살면 예정된 날짜에 도착하게 됩니다. 오늘의 선택이 내일을 바꿉니다.";

    @Override
    public String generateWarningMessage(WarningMessageRequest request) {
        return STUB_MESSAGE;
    }
}
