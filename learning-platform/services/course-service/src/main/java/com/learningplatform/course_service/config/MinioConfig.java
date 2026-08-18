package com.learningplatform.course_service.config;

import io.minio.MinioClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean(name = "minioInternalClient")
    public MinioClient minioInternalClient(
            @Value("${minio.internal-url}") String endpoint,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey) {

        System.out.println("========== CREATING MINIO INTERNAL CLIENT ==========");
        System.out.println("ENDPOINT = " + endpoint);
        System.out.println("ACCESS KEY = " + accessKey);
        System.out.println("====================================================");

        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .region("us-east-1")
                .build();
    }

    @Bean(name = "minioExternalClient")
    public MinioClient minioExternalClient(
            @Value("${minio.external-url}") String endpoint,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey) {

        System.out.println("========== CREATING MINIO EXTERNAL CLIENT ==========");
        System.out.println("ENDPOINT = " + endpoint);
        System.out.println("ACCESS KEY = " + accessKey);
        System.out.println("====================================================");

        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .region("us-east-1")
                .build();
    }
}