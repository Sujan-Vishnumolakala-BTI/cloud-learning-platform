package com.learningplatform.enroll_service.dto;

import com.learningplatform.enroll_service.entity.QuizAttemptStatus;

import java.time.LocalDateTime;

public class QuizAttemptResponse {

    private Long id;
    private Long userId;
    private Long quizId;
    private QuizAttemptStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer score;
    private Boolean passed;

    public QuizAttemptResponse(
            Long id,
            Long userId,
            Long quizId,
            QuizAttemptStatus status,
            LocalDateTime startedAt,
            LocalDateTime submittedAt,
            Integer totalQuestions,
            Integer correctAnswers,
            Integer score,
            Boolean passed) {

        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.status = status;
        this.startedAt = startedAt;
        this.submittedAt = submittedAt;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.passed = passed;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getQuizId() {
        return quizId;
    }

    public QuizAttemptStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public Integer getScore() {
        return score;
    }

    public Boolean getPassed() {
        return passed;
    }
}