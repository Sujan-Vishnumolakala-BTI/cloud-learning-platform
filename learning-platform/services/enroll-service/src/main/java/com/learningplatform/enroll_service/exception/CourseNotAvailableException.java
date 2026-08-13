package com.learningplatform.enroll_service.exception;

public class CourseNotAvailableException
        extends RuntimeException {

    public CourseNotAvailableException(String message) {
        super(message);
    }
}