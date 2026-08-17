package com.learningplatform.enroll_service.event;

import java.time.LocalDateTime;

public class CourseEnrolledEvent {

    private Long userId;
    private Long courseId;
    private LocalDateTime enrolledAt;

    public CourseEnrolledEvent() {
    }

    public CourseEnrolledEvent(
            Long userId,
            Long courseId,
            LocalDateTime enrolledAt) {

        this.userId = userId;
        this.courseId = courseId;
        this.enrolledAt = enrolledAt;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }
}