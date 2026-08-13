package com.learningplatform.user_service.exception;

public class RoleChangeNotAllowedException extends RuntimeException {

    public RoleChangeNotAllowedException(String message) {
        super(message);
    }
}