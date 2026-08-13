package com.learningplatform.course_service.exception;

public class QuestionNotFoundException
        extends RuntimeException {

    public QuestionNotFoundException(
            String message) {

        super(message);
    }
}