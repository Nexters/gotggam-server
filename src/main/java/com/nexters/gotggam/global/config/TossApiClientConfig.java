package com.nexters.gotggam.global.config;

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

    @Bean
    public RestClient tossApiRestClient(TossApiProperties properties, SslBundles sslBundles) {
        SslBundle sslBundle = sslBundles.getBundle(properties.sslBundle());
        HttpClient httpClient = HttpClient.newBuilder()
            .sslContext(sslBundle.createSslContext())
            .build();

        return RestClient.builder()
            .baseUrl(properties.baseUrl())
            .requestFactory(new JdkClientHttpRequestFactory(httpClient))
            .build();
    }
}
