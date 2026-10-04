package com.nexters.gotggam.toss.client;

import java.net.http.HttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

// 앱인토스 API는 mTLS로 호출 주체를 식별하므로, 발급받은 클라이언트 인증서를 TLS 핸드셰이크에 실어 보낸다.
@Configuration
@EnableConfigurationProperties(TossApiProperties.class)
@ConditionalOnProperty(prefix = "toss.api", name = "enabled", havingValue = "true")
public class TossApiClientConfig {

    // Boot가 구성한 RestClient.Builder를 받아 공통 메시지 컨버터(Jackson 설정)와 관측(Observation) 설정을 그대로 적용한다.
    @Bean
    public RestClient tossApiRestClient(
        RestClient.Builder builder,
        TossApiProperties properties,
        SslBundles sslBundles
    ) {
        SslBundle sslBundle = sslBundles.getBundle(properties.sslBundle());
        HttpClient httpClient = HttpClient.newBuilder()
            .sslContext(sslBundle.createSslContext())
            .build();

        return builder
            .baseUrl(properties.baseUrl())
            .requestFactory(new JdkClientHttpRequestFactory(httpClient))
            .build();
    }
}
