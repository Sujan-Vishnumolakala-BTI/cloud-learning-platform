package com.learningplatform.course_service.dto;

public class VideoUploadUrlResponse {

    private final Long lessonId;
    private final String objectKey;
    private final String uploadUrl;

    public VideoUploadUrlResponse(
            Long lessonId,
            String objectKey,
            String uploadUrl) {

        this.lessonId = lessonId;
        this.objectKey = objectKey;
        this.uploadUrl = uploadUrl;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }
}