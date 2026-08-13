package com.learningplatform.enroll_service.dto;

import jakarta.validation.constraints.NotNull;

public class StartQuizAttemptRequest {

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
}