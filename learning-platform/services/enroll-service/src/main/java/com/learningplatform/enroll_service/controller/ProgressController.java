package com.learningplatform.enroll_service.controller;

import com.learningplatform.enroll_service.dto.CourseProgressResponse;
import com.learningplatform.enroll_service.dto.CourseStudentProgressResponse;
import com.learningplatform.enroll_service.dto.LessonProgressResponse;
import com.learningplatform.enroll_service.service.ProgressService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

        private final ProgressService progressService;

        public ProgressController(
                        ProgressService progressService) {

                this.progressService = progressService;
        }

        /*
         * START LESSON
         */
        @PostMapping("/lessons/{lessonId}/start")
        public ResponseEntity<LessonProgressResponse> startLesson(
                        @PathVariable Long lessonId) {

                return ResponseEntity.ok(
                                progressService.startLesson(
                                                lessonId));
        }

        /*
         * COMPLETE LESSON
         */
        @PostMapping("/lessons/{lessonId}/complete")
        public ResponseEntity<LessonProgressResponse> completeLesson(
                        @PathVariable Long lessonId) {

                return ResponseEntity.ok(
                                progressService.completeLesson(
                                                lessonId));
        }

        /*
         * MY PROGRESS
         */
        @GetMapping("/my")
        public ResponseEntity<List<LessonProgressResponse>> getMyProgress() {

                return ResponseEntity.ok(
                                progressService.getMyProgress());
        }

        @GetMapping("/courses/{courseId}")
        public ResponseEntity<CourseProgressResponse> getCourseProgress(
                        @PathVariable Long courseId) {

                return ResponseEntity.ok(
                                progressService
                                                .getCourseProgress(courseId));
        }

        /*
         * INSTRUCTOR / ADMIN COURSE PROGRESS
         *
         * Returns progress of all enrolled students
         * for a course.
         */
        @GetMapping("/internal/courses/{courseId}/progress")
        public ResponseEntity<List<CourseStudentProgressResponse>> getInstructorCourseProgress(
                        @PathVariable Long courseId) {

                return ResponseEntity.ok(
                                progressService.getInstructorCourseProgress(courseId));
        }
}