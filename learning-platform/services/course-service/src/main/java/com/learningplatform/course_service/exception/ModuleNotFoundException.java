package com.learningplatform.course_service.exception;

public class ModuleNotFoundException extends RuntimeException {

    public ModuleNotFoundException(String message) {
        super(message);
    }
}