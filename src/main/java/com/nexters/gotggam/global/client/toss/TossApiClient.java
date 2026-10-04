package com.nexters.gotggam.global.client.toss;

import com.nexters.gotggam.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

// 앱인토스 API 호출 결과를 공통 응답 형식으로 해석해, 성공 시 success 값만 반환하고 실패 시 BusinessException으로 변환한다.
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "toss.api", name = "enabled", havingValue = "true")
public class TossApiClient {

    private static final String RATE_LIMITED_ERROR_CODE = "4095";

    private final RestClient tossApiRestClient;

    public <T> T post(
        String uri,
        Object body,
        ParameterizedTypeReference<TossApiResponse<T>> responseType
    ) {
        TossApiResponse<T> response = exchange(uri, body, responseType);

        if (response == null || !response.isSuccess()) {
            log.warn("토스 API 실패 응답: uri={}, error={}", uri, response == null ? null : response.error());
            throw new BusinessException(toErrorCode(response));
        }
        if (response.success() == null) {
            log.warn("토스 API 성공 응답에 success 값이 없음: uri={}", uri);
            throw new BusinessException(TossErrorCode.TOSS_API_FAILED);
        }
        return response.success();
    }

    // 4xx/5xx도 공통 응답 형식으로 내려올 수 있어, 상태 코드로 예외를 던지지 않고 본문을 그대로 해석한다.
    private <T> @Nullable TossApiResponse<T> exchange(
        String uri,
        Object body,
        ParameterizedTypeReference<TossApiResponse<T>> responseType
    ) {
        try {
            return tossApiRestClient.post()
                .uri(uri)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                })
                .body(responseType);
        } catch (ResourceAccessException e) {
            log.error("토스 API 연결 실패: uri={}", uri, e);
            throw new BusinessException(TossErrorCode.TOSS_API_UNAVAILABLE);
        } catch (RestClientException e) {
            log.error("토스 API 응답 처리 실패: uri={}", uri, e);
            throw new BusinessException(TossErrorCode.TOSS_API_FAILED);
        }
    }

    private TossErrorCode toErrorCode(@Nullable TossApiResponse<?> response) {
        if (response != null && response.error() != null
            && RATE_LIMITED_ERROR_CODE.equals(response.error().errorCode())) {
            return TossErrorCode.TOSS_RATE_LIMITED;
        }
        return TossErrorCode.TOSS_API_FAILED;
    }
}
