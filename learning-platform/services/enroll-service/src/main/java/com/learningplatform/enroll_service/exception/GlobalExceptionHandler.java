package com.learningplatform.enroll_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            CourseNotAvailableException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleCourseNotAvailable(
            CourseNotAvailableException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of(
                                "timestamp",
                                LocalDateTime.now(),

                                "status",
                                404,

                                "error",
                                "Course Not Available",

                                "message",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(
            AlreadyEnrolledException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleAlreadyEnrolled(
            AlreadyEnrolledException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        Map.of(
                                "timestamp",
                                LocalDateTime.now(),

                                "status",
                                409,

                                "error",
                                "Conflict",

                                "message",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(
            EnrollmentNotFoundException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleEnrollmentNotFound(
            EnrollmentNotFoundException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of(
                                "timestamp",
                                LocalDateTime.now(),

                                "status",
                                404,

                                "error",
                                "Not Found",

                                "message",
                                ex.getMessage()
                        )
                );
    }
}