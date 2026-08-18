package com.learningplatform.course_service.dto;

import jakarta.validation.constraints.NotBlank;

public class VideoUploadCompleteRequest {

    @NotBlank
    private String objectKey;

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }
}