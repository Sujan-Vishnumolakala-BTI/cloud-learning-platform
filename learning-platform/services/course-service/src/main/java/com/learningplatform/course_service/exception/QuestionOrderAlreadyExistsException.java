package com.learningplatform.course_service.exception;

public class QuestionOrderAlreadyExistsException
        extends RuntimeException {

    public QuestionOrderAlreadyExistsException(
            String message) {

        super(message);
    }
}