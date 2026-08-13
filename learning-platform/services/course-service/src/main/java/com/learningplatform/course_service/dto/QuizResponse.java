package com.learningplatform.course_service.dto;

import java.time.LocalDateTime;

public class QuizResponse {

    private Long id;
    private Long lessonId;
    private String title;
    private String description;
    private Integer passingScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public QuizResponse(
            Long id,
            Long lessonId,
            String title,
            String description,
            Integer passingScore,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.lessonId = lessonId;
        this.title = title;
        this.description = description;
        this.passingScore = passingScore;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPassingScore() {
        return passingScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}