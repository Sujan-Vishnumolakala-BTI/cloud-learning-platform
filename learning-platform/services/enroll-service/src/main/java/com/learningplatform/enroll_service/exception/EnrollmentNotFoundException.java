package com.learningplatform.enroll_service.exception;

public class EnrollmentNotFoundException extends RuntimeException{
    public EnrollmentNotFoundException(String msg){
        super(msg);
    }
}
