package com.learningplatform.course_service.dto;

import java.time.LocalDateTime;

public class ModuleResponse {

    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ModuleResponse(
            Long id,
            Long courseId,
            String title,
            String description,
            Integer orderIndex,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.orderIndex = orderIndex;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getCourseId() {
        return courseId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}