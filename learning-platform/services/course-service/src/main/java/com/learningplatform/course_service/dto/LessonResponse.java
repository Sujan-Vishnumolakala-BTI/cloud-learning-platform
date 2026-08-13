package com.learningplatform.course_service.dto;

import java.time.LocalDateTime;

public class LessonResponse {

    private Long id;
    private Long moduleId;
    private String title;
    private String description;
    private Integer orderIndex;
    private String contentType;
    private String contentUrl;
    private Integer durationMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LessonResponse(
            Long id,
            Long moduleId,
            String title,
            String description,
            Integer orderIndex,
            String contentType,
            String contentUrl,
            Integer durationMinutes,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.moduleId = moduleId;
        this.title = title;
        this.description = description;
        this.orderIndex = orderIndex;
        this.contentType = contentType;
        this.contentUrl = contentUrl;
        this.durationMinutes = durationMinutes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}