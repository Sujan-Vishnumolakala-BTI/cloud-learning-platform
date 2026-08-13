package com.learningplatform.enroll_service.dto;

import com.learningplatform.enroll_service.entity.EnrollmentStatus;

import java.time.LocalDateTime;

public class EnrollmentResponse {

    private Long id;
    private Long userId;
    private Long courseId;
    private EnrollmentStatus status;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;

    public EnrollmentResponse(
            Long id,
            Long userId,
            Long courseId,
            EnrollmentStatus status,
            LocalDateTime enrolledAt,
            LocalDateTime completedAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.status = status;
        this.enrolledAt = enrolledAt;
        this.completedAt = completedAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}