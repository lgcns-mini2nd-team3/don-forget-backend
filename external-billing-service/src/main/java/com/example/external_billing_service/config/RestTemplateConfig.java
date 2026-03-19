package com.example.external_billing_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * 타 마이크로서비스(Payment Service 등)와의 HTTP 통신을 위한 RestTemplate 빈 등록
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}