package com.learningplatform.course_service.controller;

import com.learningplatform.course_service.client.CourseStudentProgressResponse;
import com.learningplatform.course_service.client.EnrollmentServiceClient;
import com.learningplatform.course_service.client.EnrollmentStudentResponse;
import com.learningplatform.course_service.dto.QuizAttemptResponse;
import com.learningplatform.course_service.entity.Course;
import com.learningplatform.course_service.security.AuthenticatedUser;
import com.learningplatform.course_service.service.QuizService;
import com.learningplatform.course_service.repository.CourseRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/api/instructor")
public class InstructorController {

    private final CourseRepository courseRepository;
    private final EnrollmentServiceClient enrollmentServiceClient;
    private final QuizService quizService;

    public InstructorController(
            CourseRepository courseRepository,
            EnrollmentServiceClient enrollmentServiceClient,
            QuizService quizService) {

        this.courseRepository = courseRepository;

        this.enrollmentServiceClient = enrollmentServiceClient;

        this.quizService = quizService;
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getMyCourses() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new AccessDeniedException(
                    "User is not authenticated");
        }

        if (!(authentication.getPrincipal() instanceof AuthenticatedUser user)) {

            throw new AccessDeniedException(
                    "Invalid authenticated user");
        }

        boolean instructor = authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority()
                        .equals("ROLE_INSTRUCTOR"));

        if (!instructor) {

            throw new AccessDeniedException(
                    "Only instructors can access this endpoint");
        }

        Long instructorId = user.getUserId();

        return ResponseEntity.ok(
                courseRepository
                        .findByInstructorId(
                                instructorId));
    }

    // @GetMapping("/courses/{courseId}/students")
    // public ResponseEntity<List<EnrollmentStudentResponse>> getCourseStudents(
    // @PathVariable Long courseId) {

    // Course course = courseRepository.findById(courseId)
    // .orElseThrow(() -> new RuntimeException(
    // "Course not found"));

    // if (!canManageCourse(course)) {

    // throw new AccessDeniedException(
    // "You are not allowed to access this course");
    // }

    // return ResponseEntity.ok(
    // enrollmentServiceClient
    // .getStudentsByCourse(courseId));
    // }

    @GetMapping("/courses/{courseId}/students")
    public ResponseEntity<List<EnrollmentStudentResponse>> getCourseStudents(
            @PathVariable Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException(
                        "Course not found"));

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {

            throw new AccessDeniedException(
                    "Invalid authenticated user");
        }

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority()
                        .equals("ROLE_ADMIN"));

        boolean isInstructor = authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority()
                        .equals("ROLE_INSTRUCTOR"));

        if (!isAdmin && !isInstructor) {

            throw new AccessDeniedException(
                    "Only instructors or admins can access this endpoint");
        }

        if (isInstructor &&
                !course.getInstructorId()
                        .equals(user.getUserId())) {

            throw new AccessDeniedException(
                    "You are not the owner of this course");
        }

        return ResponseEntity.ok(
                enrollmentServiceClient
                        .getStudentsByCourse(courseId));
    }

    @GetMapping("/courses/{courseId}/progress")
    public ResponseEntity<List<CourseStudentProgressResponse>> getCourseProgress(
            @PathVariable Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException(
                        "Course not found"));

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {

            throw new AccessDeniedException(
                    "Invalid authenticated user");
        }

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority()
                        .equals("ROLE_ADMIN"));

        boolean isInstructor = authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority()
                        .equals("ROLE_INSTRUCTOR"));

        if (!isAdmin && !isInstructor) {

            throw new AccessDeniedException(
                    "Only instructors or admins can access this endpoint");
        }

        if (isInstructor &&
                !course.getInstructorId()
                        .equals(user.getUserId())) {

            throw new AccessDeniedException(
                    "You are not the owner of this course");
        }

        return ResponseEntity.ok(
                enrollmentServiceClient
                        .getCourseProgress(courseId));
    }

    @GetMapping("/quizzes/{quizId}/results")
    public ResponseEntity<List<QuizAttemptResponse>> getQuizResults(
            @PathVariable Long quizId) {

        Long courseId = quizService.getCourseId(quizId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException(
                        "Course not found"));

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {

            throw new AccessDeniedException(
                    "Invalid authenticated user");
        }

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority()
                        .equals("ROLE_ADMIN"));

        boolean isInstructor = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority()
                        .equals("ROLE_INSTRUCTOR"));

        if (!isAdmin && !isInstructor) {

            throw new AccessDeniedException(
                    "Only instructors or admins can access quiz results");
        }

        if (isInstructor &&
                !course.getInstructorId()
                        .equals(user.getUserId())) {

            throw new AccessDeniedException(
                    "You are not the owner of this course");
        }

        return ResponseEntity.ok(
                enrollmentServiceClient
                        .getQuizResults(quizId));
    }

    
}