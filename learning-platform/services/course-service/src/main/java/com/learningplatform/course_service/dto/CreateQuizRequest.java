package com.learningplatform.course_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateQuizRequest {

    @NotBlank(message = "Quiz title is required")
    private String title;

    private String description;

    @NotNull(message = "Passing score is required")
    @Min(value = 1, message = "Passing score must be at least 1")
    @Max(value = 100, message = "Passing score cannot exceed 100")
    private Integer passingScore;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
    }
}