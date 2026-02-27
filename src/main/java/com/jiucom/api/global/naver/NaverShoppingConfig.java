package com.jiucom.api.global.naver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class NaverShoppingConfig {

    @Value("${naver.shopping.client-id:}")
    private String clientId;

    @Value("${naver.shopping.client-secret:}")
    private String clientSecret;

    @SuppressWarnings("removal")
    @Bean("naverShoppingRestTemplate")
    public RestTemplate naverShoppingRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(java.time.Duration.ofSeconds(5))
                .setReadTimeout(java.time.Duration.ofSeconds(10))
                .build();
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public boolean isConfigured() {
        return clientId != null && !clientId.isBlank()
                && clientSecret != null && !clientSecret.isBlank();
    }
}
