package com.learningplatform.course_service.dto;

public class QuestionOptionResponse {

    private Long id;
    private Long questionId;
    private String optionText;
    private boolean correct;

    public QuestionOptionResponse(
            Long id,
            Long questionId,
            String optionText,
            boolean correct) {

        this.id = id;
        this.questionId = questionId;
        this.optionText = optionText;
        this.correct = correct;
    }

    public Long getId() {
        return id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public boolean isCorrect() {
        return correct;
    }
}