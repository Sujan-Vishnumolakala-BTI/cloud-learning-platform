package com.learningplatform.course_service.exception;

public class LessonOrderAlreadyExistsException
        extends RuntimeException {

    public LessonOrderAlreadyExistsException(String message) {
        super(message);
    }
}