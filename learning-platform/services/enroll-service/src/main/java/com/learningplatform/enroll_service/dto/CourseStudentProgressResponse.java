package com.learningplatform.enroll_service.dto;

public class CourseStudentProgressResponse {

    private Long userId;
    private Long courseId;
    private long totalLessons;
    private long completedLessons;
    private double progressPercentage;
    private String status;

    public CourseStudentProgressResponse(
            Long userId,
            Long courseId,
            long totalLessons,
            long completedLessons,
            double progressPercentage,
            String status) {

        this.userId = userId;
        this.courseId = courseId;
        this.totalLessons = totalLessons;
        this.completedLessons = completedLessons;
        this.progressPercentage = progressPercentage;
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCourseId() {
        return courseId;
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