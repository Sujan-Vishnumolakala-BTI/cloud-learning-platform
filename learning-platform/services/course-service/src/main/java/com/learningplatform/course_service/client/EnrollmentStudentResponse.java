package com.learningplatform.course_service.client;

import java.time.LocalDateTime;

public class EnrollmentStudentResponse {

    private Long userId;
    private Long courseId;
    private String status;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;

    public Long getUserId() {
        return userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}