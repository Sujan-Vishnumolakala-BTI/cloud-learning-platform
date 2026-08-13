package com.learningplatform.enroll_service.dto;

import com.learningplatform.enroll_service.entity.EnrollmentStatus;

import java.time.LocalDateTime;

public class InstructorStudentResponse {

    private Long userId;
    private Long courseId;
    private EnrollmentStatus status;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;

    public InstructorStudentResponse(
            Long userId,
            Long courseId,
            EnrollmentStatus status,
            LocalDateTime enrolledAt,
            LocalDateTime completedAt) {

        this.userId = userId;
        this.courseId = courseId;
        this.status = status;
        this.enrolledAt = enrolledAt;
        this.completedAt = completedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}