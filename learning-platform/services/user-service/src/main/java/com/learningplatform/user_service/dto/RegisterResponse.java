package com.learningplatform.user_service.dto;

public class RegisterResponse {

    private String email;
    private String message;

    public RegisterResponse() {
    }

    public RegisterResponse(
            String email,
            String message) {

        this.email = email;
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }
}