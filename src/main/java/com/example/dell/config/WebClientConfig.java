package com.example.dell.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${amazon.base-url}")
    private String amazonBaseUrl;

    @Bean
    public WebClient amazonWebClient() {
        return WebClient.builder()
                .baseUrl(amazonBaseUrl)
                .build();
    }
}