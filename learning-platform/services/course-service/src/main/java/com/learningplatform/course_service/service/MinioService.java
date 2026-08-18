package com.learningplatform.course_service.service;

import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.http.Method;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    private final MinioClient internalClient;
    private final MinioClient externalClient;
    private final String bucket;

    public MinioService(
            @Qualifier("minioInternalClient")
            MinioClient internalClient,

            @Qualifier("minioExternalClient")
            MinioClient externalClient,

            @Value("${minio.bucket}")
            String bucket) {

        this.internalClient = internalClient;
        this.externalClient = externalClient;
        this.bucket = bucket;
    }

    /**
     * Generate PUT URL for browser upload.
     *
     * IMPORTANT:
     * This must use externalClient because the browser
     * accesses MinIO through localhost:9000.
     */
    public String generateUploadUrl(String objectKey)
            throws Exception {

        return externalClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(bucket)
                        .object(objectKey)
                        .expiry(1, TimeUnit.HOURS)
                        .build()
        );
    }

    /**
     * Generate GET URL for browser playback/download.
     */
    public String generateDownloadUrl(String objectKey)
            throws Exception {

        return externalClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucket)
                        .object(objectKey)
                        .expiry(1, TimeUnit.HOURS)
                        .build()
        );
    }

    /**
     * Internal server-side object access.
     *
     * Container -> MinIO uses minio:9000.
     */
    public InputStream getObject(String objectKey)
            throws Exception {

        return internalClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .build()
        );
    }

    /**
     * Internal server-side object metadata.
     */
    public StatObjectResponse statObject(String objectKey)
            throws Exception {

        return internalClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .build()
        );
    }
}