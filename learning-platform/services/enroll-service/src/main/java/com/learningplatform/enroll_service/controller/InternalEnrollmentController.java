// package com.learningplatform.enroll_service.controller;

// import com.learningplatform.enroll_service.dto.InstructorStudentResponse;
// import com.learningplatform.enroll_service.service.EnrollmentService;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/internal")
// public class InternalEnrollmentController {

//     private final EnrollmentService enrollmentService;

//     public InternalEnrollmentController(
//             EnrollmentService enrollmentService) {

//         this.enrollmentService =
//                 enrollmentService;
//     }

//     @GetMapping("/courses/{courseId}/students")
//     public ResponseEntity<List<InstructorStudentResponse>>
//     getStudentsByCourse(
//             @PathVariable Long courseId) {

//         return ResponseEntity.ok(
//                 enrollmentService
//                         .getStudentsByCourse(courseId));
//     }
// }

package com.learningplatform.enroll_service.controller;

import com.learningplatform.enroll_service.dto.CourseStudentProgressResponse;
import com.learningplatform.enroll_service.dto.InstructorStudentResponse;
import com.learningplatform.enroll_service.dto.QuizAttemptResponse;
import com.learningplatform.enroll_service.service.EnrollmentService;
import com.learningplatform.enroll_service.service.ProgressService;
import com.learningplatform.enroll_service.service.QuizAttemptService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internal")
public class InternalEnrollmentController {

    private final EnrollmentService enrollmentService;
    private final ProgressService progressService;
    private final QuizAttemptService quizAttemptService;

    public InternalEnrollmentController(
            EnrollmentService enrollmentService,
            ProgressService progressService,QuizAttemptService quizAttemptService) {

        this.enrollmentService = enrollmentService;

        this.progressService = progressService;
		this.quizAttemptService = quizAttemptService;
    }

    @GetMapping("/courses/{courseId}/students")
    public ResponseEntity<List<InstructorStudentResponse>> getStudentsByCourse(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                enrollmentService
                        .getStudentsByCourse(courseId));
    }

    @GetMapping("/courses/{courseId}/progress")
    public ResponseEntity<List<CourseStudentProgressResponse>> getCourseProgress(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                progressService
                        .getInstructorCourseProgress(
                                courseId));
    }

    @GetMapping("/quizzes/{quizId}/results")
    public ResponseEntity<List<QuizAttemptResponse>> getQuizResults(
            @PathVariable Long quizId) {

        return ResponseEntity.ok(
                quizAttemptService
                        .getQuizResults(quizId));
    }
}