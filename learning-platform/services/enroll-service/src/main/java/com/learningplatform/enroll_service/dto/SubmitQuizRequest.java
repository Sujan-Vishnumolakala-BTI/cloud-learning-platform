package com.learningplatform.enroll_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class SubmitQuizRequest {

    @NotEmpty(message = "Answers are required")
    @Valid
    private List<QuizAnswerRequest> answers;

    public List<QuizAnswerRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(
            List<QuizAnswerRequest> answers) {

        this.answers = answers;
    }
}