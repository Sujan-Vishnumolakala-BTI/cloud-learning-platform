package com.learningplatform.enroll_service.controller;

import com.learningplatform.enroll_service.service.EnrollmentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/enrollments")
public class AdminEnrollmentController {

    private final EnrollmentService enrollmentService;

    public AdminEnrollmentController(
            EnrollmentService enrollmentService) {

        this.enrollmentService =
                enrollmentService;
    }

    /*
     * ADMIN DASHBOARD ENROLLMENT STATS
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {

        return ResponseEntity.ok(
                enrollmentService
                        .getAdminEnrollmentStats()
        );
    }
}