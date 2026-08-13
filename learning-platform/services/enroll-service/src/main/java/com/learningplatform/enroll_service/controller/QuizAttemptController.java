package com.learningplatform.enroll_service.controller;

import com.learningplatform.enroll_service.dto.QuizAttemptResponse;
import com.learningplatform.enroll_service.dto.StartQuizAttemptRequest;
import com.learningplatform.enroll_service.dto.SubmitQuizRequest;
import com.learningplatform.enroll_service.service.QuizAttemptService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz-attempts")
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;

    public QuizAttemptController(
            QuizAttemptService quizAttemptService) {

        this.quizAttemptService =
                quizAttemptService;
    }

    @PostMapping
    public ResponseEntity<QuizAttemptResponse>
    startAttempt(
            @Valid @RequestBody
            StartQuizAttemptRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        quizAttemptService
                                .startAttempt(request));
    }

    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<QuizAttemptResponse>
    submitAttempt(
            @PathVariable Long attemptId,
            @Valid @RequestBody
            SubmitQuizRequest request) {

        return ResponseEntity.ok(
                quizAttemptService.submitAttempt(
                        attemptId,
                        request));
    }

    @GetMapping("/quiz/{quizId}/my")
    public ResponseEntity<List<QuizAttemptResponse>>
    getMyAttempts(
            @PathVariable Long quizId) {

        return ResponseEntity.ok(
                quizAttemptService
                        .getMyAttempts(quizId));
    }
}