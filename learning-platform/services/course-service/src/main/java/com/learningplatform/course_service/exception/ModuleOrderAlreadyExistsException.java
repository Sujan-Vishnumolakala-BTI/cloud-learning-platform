package com.learningplatform.course_service.exception;

public class ModuleOrderAlreadyExistsException
        extends RuntimeException {

    public ModuleOrderAlreadyExistsException(String message) {
        super(message);
    }
}