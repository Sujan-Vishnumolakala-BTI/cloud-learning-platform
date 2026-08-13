package com.learningplatform.course_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(CourseNotFoundException.class)
        public ResponseEntity<Map<String, Object>> handleCourseNotFound(
                        CourseNotFoundException ex) {

                return buildResponse(
                                HttpStatus.NOT_FOUND,
                                "Course not found",
                                ex.getMessage());
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<Map<String, Object>> handleAccessDenied(
                        AccessDeniedException ex) {

                return buildResponse(
                                HttpStatus.FORBIDDEN,
                                "Forbidden",
                                ex.getMessage());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidation(
                        MethodArgumentNotValidException ex) {

                Map<String, Object> response = new HashMap<>();

                response.put("timestamp", LocalDateTime.now());
                response.put("status", 400);
                response.put("error", "Bad Request");
                response.put("message", "Validation failed");

                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult()
                                .getFieldErrors()
                                .forEach(error -> errors.put(
                                                error.getField(),
                                                error.getDefaultMessage()));

                response.put("errors", errors);

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(response);
        }

        private ResponseEntity<Map<String, Object>> buildResponse(
                        HttpStatus status,
                        String error,
                        String message) {

                Map<String, Object> response = new HashMap<>();

                response.put(
                                "timestamp",
                                LocalDateTime.now());

                response.put(
                                "status",
                                status.value());

                response.put(
                                "error",
                                error);

                response.put(
                                "message",
                                message);

                return ResponseEntity
                                .status(status)
                                .body(response);
        }

        @ExceptionHandler(CourseAlreadyPublishedException.class)
        public ResponseEntity<Map<String, String>> handleCourseAlreadyPublished(
                        CourseAlreadyPublishedException ex) {

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(Map.of(
                                                "error", ex.getMessage()));
        }

        @ExceptionHandler(CourseStateException.class)
        public ResponseEntity<Map<String, Object>> handleCourseStateException(
                        CourseStateException ex) {

                Map<String, Object> body = new HashMap<>();

                body.put("status", 409);
                body.put("error", "CONFLICT");
                body.put("message", ex.getMessage());
                body.put("timestamp", LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(body);
        }

        @ExceptionHandler(ModuleOrderAlreadyExistsException.class)
        public ResponseEntity<Map<String, Object>> handleModuleOrderAlreadyExists(
                        ModuleOrderAlreadyExistsException ex) {

                Map<String, Object> response = Map.of(
                                "timestamp", LocalDateTime.now(),
                                "status", HttpStatus.CONFLICT.value(),
                                "error", "Conflict",
                                "message", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(response);
        }

        @ExceptionHandler(ModuleNotFoundException.class)
        public ResponseEntity<Map<String, Object>> handleModuleNotFound(ModuleNotFoundException ex) {

                Map<String, Object> response = Map.of(
                                "timestamp", LocalDateTime.now(),
                                "status", HttpStatus.NOT_FOUND.value(),
                                "error", "Not Found",
                                "message", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(response);
        }

        @ExceptionHandler(LessonNotFoundException.class)
        public ResponseEntity<Map<String, Object>> handleLessonNotFound(LessonNotFoundException ex) {

                Map<String, Object> response = Map.of(
                                "timestamp", LocalDateTime.now(),
                                "status", HttpStatus.NOT_FOUND.value(),
                                "error", "Not Found",
                                "message", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(response);
        }

        @ExceptionHandler(LessonOrderAlreadyExistsException.class)
        public ResponseEntity<Map<String, Object>> handleLessonOrderAlreadyExists(
                        LessonOrderAlreadyExistsException ex) {

                Map<String, Object> response = Map.of(
                                "timestamp", LocalDateTime.now(),
                                "status", HttpStatus.CONFLICT.value(),
                                "error", "Conflict",
                                "message", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(response);
        }

        @ExceptionHandler(QuestionOrderAlreadyExistsException.class)
        public ResponseEntity<Map<String, Object>> handleQuestionOrderAlreadyExists(
                        QuestionOrderAlreadyExistsException ex) {

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(Map.of(
                                                "timestamp",
                                                LocalDateTime.now(),

                                                "status",
                                                409,

                                                "error",
                                                "Question Order Already Exists",

                                                "message",
                                                ex.getMessage()));
        }

}