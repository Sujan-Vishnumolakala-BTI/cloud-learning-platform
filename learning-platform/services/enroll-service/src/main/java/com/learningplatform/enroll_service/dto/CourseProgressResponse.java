package com.learningplatform.enroll_service.dto;

public class CourseProgressResponse {

    private Long courseId;
    private Long userId;
    private long totalLessons;
    private long completedLessons;
    private double progressPercentage;
    private String status;

    public CourseProgressResponse(
            Long courseId,
            Long userId,
            long totalLessons,
            long completedLessons,
            double progressPercentage,
            String status) {

        this.courseId = courseId;
        this.userId = userId;
        this.totalLessons = totalLessons;
        this.completedLessons = completedLessons;
        this.progressPercentage = progressPercentage;
        this.status = status;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Long getUserId() {
        return userId;
    }

    public long getTotalLessons() {
        return totalLessons;
    }

    public long getCompletedLessons() {
        return completedLessons;
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public String getStatus() {
        return status;
    }
}