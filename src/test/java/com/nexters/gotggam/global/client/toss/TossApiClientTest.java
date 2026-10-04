package com.nexters.gotggam.global.client.toss;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.nexters.gotggam.global.exception.BusinessException;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class TossApiClientTest {

    private static final String BASE_URL = "https://apps-in-toss-api.toss.im";
    private static final String URI = "/api-partner/v1/sample";
    private static final ParameterizedTypeReference<TossApiResponse<SampleResponse>> RESPONSE_TYPE =
        new ParameterizedTypeReference<>() {
        };

    private MockRestServiceServer server;
    private TossApiClient client;

    record SampleResponse(String value) {

    }

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        client = new TossApiClient(builder.build());
    }

    private void respond(HttpStatus status, String body) {
        server.expect(requestTo(BASE_URL + URI))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(status).contentType(MediaType.APPLICATION_JSON).body(body));
    }

    private void assertThrowsWith(TossErrorCode errorCode) {
        assertThatThrownBy(() -> client.post(URI, Map.of(), RESPONSE_TYPE))
            .isInstanceOf(BusinessException.class)
            .extracting(e -> ((BusinessException) e).getBaseError())
            .isEqualTo(errorCode);
    }

    @Test
    @DisplayName("resultType이 SUCCESS면 success 값만 반환한다")
    void returnsSuccessValue() {
        server.expect(requestTo(BASE_URL + URI))
            .andRespond(withSuccess("""
                {"resultType":"SUCCESS","success":{"value":"ok"}}
                """, MediaType.APPLICATION_JSON));

        SampleResponse result = client.post(URI, Map.of(), RESPONSE_TYPE);

        assertThat(result.value()).isEqualTo("ok");
    }

    @Test
    @DisplayName("HTTP 200이어도 resultType이 FAIL이면 TOSS_API_FAILED 예외를 던진다")
    void throwsOnFailResultWithHttp200() {
        respond(HttpStatus.OK, """
            {"resultType":"FAIL","success":null,"error":{"errorType":0,"errorCode":"4050","reason":"인증서버에 등록된 미니앱이 아닙니다.","data":{},"title":null}}
            """);

        assertThrowsWith(TossErrorCode.TOSS_API_FAILED);
    }

    @Test
    @DisplayName("요청 한도 초과(4095)면 TOSS_RATE_LIMITED 예외를 던진다")
    void throwsRateLimitedOn4095() {
        respond(HttpStatus.OK, """
            {"resultType":"FAIL","error":{"errorCode":"4095","reason":"요청 한도를 초과했습니다."}}
            """);

        assertThrowsWith(TossErrorCode.TOSS_RATE_LIMITED);
    }

    @Test
    @DisplayName("4xx/5xx가 공통 응답 형식으로 오면 본문의 에러로 처리한다")
    void handlesCommonFormatOnHttpError() {
        respond(HttpStatus.BAD_REQUEST, """
            {"resultType":"FAIL","error":{"errorCode":"4095","reason":"요청 한도를 초과했습니다."}}
            """);

        assertThrowsWith(TossErrorCode.TOSS_RATE_LIMITED);
    }

    @Test
    @DisplayName("4xx/5xx 본문이 공통 응답 형식이 아니면 TOSS_API_FAILED 예외를 던진다")
    void throwsFailedOnNonJsonErrorBody() {
        server.expect(requestTo(BASE_URL + URI))
            .andRespond(withStatus(HttpStatus.BAD_GATEWAY).contentType(MediaType.TEXT_HTML).body("<html>Bad Gateway</html>"));

        assertThrowsWith(TossErrorCode.TOSS_API_FAILED);
    }

    @Test
    @DisplayName("SUCCESS인데 success 값이 없으면 TOSS_API_FAILED 예외를 던진다")
    void throwsFailedOnMissingSuccessValue() {
        respond(HttpStatus.OK, """
            {"resultType":"SUCCESS","success":null}
            """);

        assertThrowsWith(TossErrorCode.TOSS_API_FAILED);
    }

    @Test
    @DisplayName("연결에 실패하면 TOSS_API_UNAVAILABLE 예외를 던진다")
    void throwsUnavailableOnConnectionFailure() {
        server.expect(requestTo(BASE_URL + URI))
            .andRespond(withException(new IOException("connection refused")));

        assertThrowsWith(TossErrorCode.TOSS_API_UNAVAILABLE);
    }
}
