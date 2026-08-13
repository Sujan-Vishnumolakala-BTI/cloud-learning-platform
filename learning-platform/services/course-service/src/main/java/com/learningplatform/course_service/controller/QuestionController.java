package com.learningplatform.course_service.controller;

import com.learningplatform.course_service.dto.CreateQuestionRequest;
import com.learningplatform.course_service.dto.InternalQuizAnswerResponse;
import com.learningplatform.course_service.dto.CreateQuestionOptionRequest;
import com.learningplatform.course_service.dto.QuestionResponse;
import com.learningplatform.course_service.dto.QuestionOptionResponse;
import com.learningplatform.course_service.service.QuestionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(
            QuestionService questionService) {

        this.questionService = questionService;
    }

    @PostMapping("/quizzes/{quizId}/questions")
    public ResponseEntity<QuestionResponse> createQuestion(
            @PathVariable Long quizId,
            @Valid @RequestBody CreateQuestionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        questionService.createQuestion(
                                quizId,
                                request));
    }

    @GetMapping("/quizzes/{quizId}/questions")
    public ResponseEntity<List<QuestionResponse>> getQuestions(
            @PathVariable Long quizId) {

        return ResponseEntity.ok(
                questionService.getQuestions(
                        quizId));
    }

    @PostMapping("/questions/{questionId}/options")
    public ResponseEntity<QuestionOptionResponse> createOption(
            @PathVariable Long questionId,
            @Valid @RequestBody CreateQuestionOptionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        questionService.createOption(
                                questionId,
                                request));
    }

    @GetMapping("/questions/{questionId}/options")
    public ResponseEntity<List<QuestionOptionResponse>> getOptions(
            @PathVariable Long questionId) {

        return ResponseEntity.ok(
                questionService.getOptions(
                        questionId));
    }

    @GetMapping("/internal/quizzes/{quizId}/answers")
    public ResponseEntity<InternalQuizAnswerResponse> getQuizAnswers(
            @PathVariable Long quizId) {

        return ResponseEntity.ok(
                questionService.getQuizAnswers(
                        quizId));
    }
}