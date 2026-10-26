package com.nexters.gotggam.toss.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Map;
import javax.net.ssl.SSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class TossApiClientConfigTest {

    private static final long SLOW_RESPONSE_MILLIS = 2_000;

    @Mock
    private SslBundles sslBundles;

    @Mock
    private SslBundle sslBundle;

    private HttpServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/fast", exchange -> respond(exchange, "ok"));
        server.createContext("/slow", exchange -> {
            try {
                Thread.sleep(SLOW_RESPONSE_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            respond(exchange, "late");
        });
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    @DisplayName("타임아웃을 설정하지 않으면 연결 3초, 응답 5초를 기본값으로 사용한다")
    void usesDefaultTimeouts() {
        TossApiProperties properties = new TossApiProperties(true, null, null, null, null);

        assertThat(properties.connectTimeout()).isEqualTo(Duration.ofSeconds(3));
        assertThat(properties.readTimeout()).isEqualTo(Duration.ofSeconds(5));
    }

    @Test
    @DisplayName("설정 파일의 단위 표기(1s, 500ms)를 Duration으로 바인딩한다")
    void bindsTimeoutsFromProperties() {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource(Map.of(
            "toss.api.connect-timeout", "1s",
            "toss.api.read-timeout", "500ms"
        ));

        TossApiProperties properties = new Binder(source)
            .bind("toss.api", TossApiProperties.class)
            .get();

        assertThat(properties.connectTimeout()).isEqualTo(Duration.ofSeconds(1));
        assertThat(properties.readTimeout()).isEqualTo(Duration.ofMillis(500));
    }

    @Test
    @DisplayName("응답 타임아웃 안에 응답하면 정상적으로 결과를 받는다")
    void returnsResponseWithinReadTimeout() throws NoSuchAlgorithmException {
        RestClient restClient = createRestClient(Duration.ofMillis(500));

        String body = restClient.get().uri("/fast").retrieve().body(String.class);

        assertThat(body).isEqualTo("ok");
    }

    @Test
    @DisplayName("응답 타임아웃을 넘기면 응답을 기다리지 않고 예외를 던진다")
    void throwsWhenReadTimeoutExceeded() throws NoSuchAlgorithmException {
        RestClient restClient = createRestClient(Duration.ofMillis(500));

        long start = System.nanoTime();
        assertThatThrownBy(() -> restClient.get().uri("/slow").retrieve().body(String.class))
            .isInstanceOf(ResourceAccessException.class)
            .hasRootCauseInstanceOf(HttpTimeoutException.class);
        long elapsedMillis = Duration.ofNanos(System.nanoTime() - start).toMillis();

        assertThat(elapsedMillis).isLessThan(SLOW_RESPONSE_MILLIS);
    }

    private RestClient createRestClient(Duration readTimeout) throws NoSuchAlgorithmException {
        given(sslBundles.getBundle("toss")).willReturn(sslBundle);
        given(sslBundle.createSslContext()).willReturn(SSLContext.getDefault());

        TossApiProperties properties = new TossApiProperties(
            true,
            "http://localhost:" + server.getAddress().getPort(),
            null,
            Duration.ofSeconds(1),
            readTimeout
        );

        return new TossApiClientConfig().tossApiRestClient(RestClient.builder(), properties, sslBundles);
    }

    private static void respond(com.sun.net.httpserver.HttpExchange exchange, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
