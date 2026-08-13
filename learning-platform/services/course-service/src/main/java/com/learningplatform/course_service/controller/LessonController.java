package com.learningplatform.course_service.controller;

import com.learningplatform.course_service.dto.CourseLessonResponse;
import com.learningplatform.course_service.dto.CreateLessonRequest;
import com.learningplatform.course_service.dto.LessonResponse;
import com.learningplatform.course_service.dto.UpdateLessonRequest;
import com.learningplatform.course_service.service.LessonService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LessonController {

        private final LessonService lessonService;

        public LessonController(
                        LessonService lessonService) {

                this.lessonService = lessonService;
        }

        /*
         * CREATE LESSON
         */
        @PostMapping("/modules/{moduleId}/lessons")
        public ResponseEntity<LessonResponse> createLesson(
                        @PathVariable Long moduleId,
                        @Valid @RequestBody CreateLessonRequest request) {

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                lessonService.createLesson(
                                                                moduleId,
                                                                request));
        }

        /*
         * GET MODULE LESSONS
         */
        @GetMapping("/modules/{moduleId}/lessons")
        public ResponseEntity<List<LessonResponse>> getLessonsByModule(
                        @PathVariable Long moduleId) {

                return ResponseEntity.ok(
                                lessonService
                                                .getLessonsByModule(moduleId));
        }

        /*
         * GET LESSON
         */
        @GetMapping("/lessons/{id}")
        public ResponseEntity<LessonResponse> getLesson(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                lessonService.getLesson(id));
        }

        /*
         * UPDATE LESSON
         */
        @PutMapping("/lessons/{id}")
        public ResponseEntity<LessonResponse> updateLesson(
                        @PathVariable Long id,
                        @Valid @RequestBody UpdateLessonRequest request) {

                return ResponseEntity.ok(
                                lessonService.updateLesson(
                                                id,
                                                request));
        }

        /*
         * DELETE LESSON
         */
        @DeleteMapping("/lessons/{id}")
        public ResponseEntity<Void> deleteLesson(
                        @PathVariable Long id) {

                lessonService.deleteLesson(id);

                return ResponseEntity.noContent().build();
        }

        @GetMapping("/courses/{courseId}/lessons")
        public ResponseEntity<List<CourseLessonResponse>> getLessonsByCourse(
                        @PathVariable Long courseId) {

                return ResponseEntity.ok(
                                lessonService
                                                .getLessonsByCourse(courseId));
        }

        @GetMapping("/lessons/{lessonId}/course")
        public ResponseEntity<Long> getCourseId(
                        @PathVariable Long lessonId) {

                return ResponseEntity.ok(
                                lessonService.getCourseId(
                                                lessonId));
        }

        @GetMapping("/courses/{courseId}/lesson-count")
        public ResponseEntity<Long> getLessonCount(
                        @PathVariable Long courseId) {

                return ResponseEntity.ok(
                                lessonService.getLessonCount(
                                                courseId));
        }
}