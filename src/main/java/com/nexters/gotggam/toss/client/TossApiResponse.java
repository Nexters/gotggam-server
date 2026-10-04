package com.nexters.gotggam.toss.client;

import org.jspecify.annotations.Nullable;

// 앱인토스 API 공통 응답. HTTP 200이어도 resultType이 FAIL일 수 있으므로 반드시 resultType으로 성공 여부를 판단한다.
public record TossApiResponse<T>(
    String resultType,
    @Nullable T success,
    @Nullable TossApiError error
) {

    private static final String SUCCESS = "SUCCESS";

    public boolean isSuccess() {
        return SUCCESS.equals(resultType);
    }
}
