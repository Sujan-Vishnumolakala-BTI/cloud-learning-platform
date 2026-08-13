package com.learningplatform.course_service.dto;

import java.time.LocalDateTime;

public class QuestionResponse {

    private Long id;
    private Long quizId;
    private String questionText;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public QuestionResponse(
            Long id,
            Long quizId,
            String questionText,
            Integer orderIndex,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.quizId = quizId;
        this.questionText = questionText;
        this.orderIndex = orderIndex;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getQuizId() {
        return quizId;
    }

    public String getQuestionText() {
        return questionText;
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