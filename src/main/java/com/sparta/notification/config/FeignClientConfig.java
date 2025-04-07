package com.sparta.notification.config;

import com.sparta.notification.domain.common.AuthTokenHolder;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String token = AuthTokenHolder.getToken(); // 직접 만든 ThreadLocal
            if (token != null && !token.isBlank()) {
                requestTemplate.header("Authorization", token);
            }
        };
    }
}
