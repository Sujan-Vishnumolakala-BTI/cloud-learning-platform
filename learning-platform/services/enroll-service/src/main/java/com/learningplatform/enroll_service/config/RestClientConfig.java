package com.learningplatform.enroll_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

     @Value("${course-service.url}")
    private String courseServiceUrl;

    @Bean
    public RestClient restClient() {

        return RestClient.builder()
                .baseUrl(courseServiceUrl)
                .build();
    }
}