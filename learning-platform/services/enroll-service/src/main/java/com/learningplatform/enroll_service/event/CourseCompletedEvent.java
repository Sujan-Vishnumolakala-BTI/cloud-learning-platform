package com.learningplatform.enroll_service.event;

import java.time.LocalDateTime;

public class CourseCompletedEvent {

    private Long userId;
    private Long courseId;
    private LocalDateTime completedAt;

    public CourseCompletedEvent() {
    }

    public CourseCompletedEvent(
            Long userId,
            Long courseId,
            LocalDateTime completedAt) {

        this.userId = userId;
        this.courseId = courseId;
        this.completedAt = completedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}