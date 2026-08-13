package com.learningplatform.enroll_service.controller;

import com.learningplatform.enroll_service.dto.CreateEnrollmentRequest;
import com.learningplatform.enroll_service.dto.EnrollmentResponse;
import com.learningplatform.enroll_service.dto.InstructorStudentResponse;
import com.learningplatform.enroll_service.service.EnrollmentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(
            EnrollmentService enrollmentService) {

        this.enrollmentService =
                enrollmentService;
    }

    /*
     * ENROLL
     */
    @PostMapping
    public ResponseEntity<EnrollmentResponse> enroll(
            @Valid @RequestBody
            CreateEnrollmentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                    enrollmentService.enroll(request)
                );
    }

    /*
     * MY ENROLLMENTS
     */
    @GetMapping("/my")
    public ResponseEntity<List<EnrollmentResponse>>
    getMyEnrollments() {

        return ResponseEntity.ok(
                enrollmentService
                        .getMyEnrollments());
    }

    /*
     * GET ENROLLMENT
     */
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse>
    getEnrollment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                enrollmentService
                        .getEnrollment(id));
    }

    /*
     * CANCEL
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    cancelEnrollment(
            @PathVariable Long id) {

        enrollmentService
                .cancelEnrollment(id);

        return ResponseEntity.noContent()
                .build();
    }

    
}