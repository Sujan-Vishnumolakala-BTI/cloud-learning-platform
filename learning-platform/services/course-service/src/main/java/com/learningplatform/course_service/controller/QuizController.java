package com.learningplatform.course_service.controller;

import com.learningplatform.course_service.dto.CreateQuizRequest;
import com.learningplatform.course_service.dto.QuizResponse;
import com.learningplatform.course_service.service.QuizService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class QuizController {

    private final QuizService quizService;

    public QuizController(
            QuizService quizService) {

        this.quizService = quizService;
    }


    /*
     * CREATE QUIZ
     */
    @PostMapping("/lessons/{lessonId}/quiz")
    public ResponseEntity<QuizResponse> createQuiz(
            @PathVariable Long lessonId,
            @Valid @RequestBody CreateQuizRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        quizService.createQuiz(
                                lessonId,
                                request));
    }


    /*
     * GET QUIZ FOR LESSON
     */
    @GetMapping("/lessons/{lessonId}/quiz")
    public ResponseEntity<QuizResponse> getQuiz(
            @PathVariable Long lessonId) {

        return ResponseEntity.ok(
                quizService.getQuizByLesson(
                        lessonId));
    }


    /*
     * GET ALL QUIZZES FOR COURSE
     */
    @GetMapping("/courses/{courseId}/quizzes")
    public ResponseEntity<List<QuizResponse>>
    getQuizzesByCourse(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                quizService.getQuizzesByCourse(
                        courseId));
    }


    /*
     * GET QUIZ BY ID
     */
    @GetMapping("/quizzes/{quizId}")
    public ResponseEntity<QuizResponse> getQuizById(
            @PathVariable Long quizId) {

        return ResponseEntity.ok(
                quizService.getQuizById(
                        quizId));
    }


    /*
     * GET COURSE ID
     */
    @GetMapping("/quizzes/{quizId}/course")
    public ResponseEntity<Long> getCourseId(
            @PathVariable Long quizId) {

        return ResponseEntity.ok(
                quizService.getCourseId(
                        quizId));
    }


    /*
     * UPDATE QUIZ
     */
    @PutMapping("/quizzes/{quizId}")
    public ResponseEntity<QuizResponse> updateQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody CreateQuizRequest request) {

        return ResponseEntity.ok(
                quizService.updateQuiz(
                        quizId,
                        request));
    }


    /*
     * DELETE QUIZ
     */
    @DeleteMapping("/quizzes/{quizId}")
    public ResponseEntity<Void> deleteQuiz(
            @PathVariable Long quizId) {

        quizService.deleteQuiz(
                quizId);

        return ResponseEntity
                .noContent()
                .build();
    }
}