package com.learningplatform.course_service.dto;

import java.util.List;

public class InternalQuizAnswerResponse {

    private Long quizId;
    private Integer passingScore;
    private List<QuestionAnswerData> questions;

    public InternalQuizAnswerResponse(
            Long quizId,
            Integer passingScore,
            List<QuestionAnswerData> questions) {

        this.quizId = quizId;
        this.passingScore = passingScore;
        this.questions = questions;
    }

    public Long getQuizId() {
        return quizId;
    }

    public Integer getPassingScore() {
        return passingScore;
    }

    public List<QuestionAnswerData> getQuestions() {
        return questions;
    }

    public static class QuestionAnswerData {

        private Long questionId;
        private Long correctOptionId;

        public QuestionAnswerData(
                Long questionId,
                Long correctOptionId) {

            this.questionId = questionId;
            this.correctOptionId = correctOptionId;
        }

        public Long getQuestionId() {
            return questionId;
        }

        public Long getCorrectOptionId() {
            return correctOptionId;
        }
    }
}