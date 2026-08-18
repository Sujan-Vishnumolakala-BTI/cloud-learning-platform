package com.learningplatform.course_service.dto;

public class VideoUrlResponse {

    private final Long lessonId;
    private final String videoUrl;

    public VideoUrlResponse(
            Long lessonId,
            String videoUrl) {

        this.lessonId = lessonId;
        this.videoUrl = videoUrl;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}