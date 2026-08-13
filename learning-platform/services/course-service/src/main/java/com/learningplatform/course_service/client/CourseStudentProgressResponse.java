package com.learningplatform.course_service.client;

public class CourseStudentProgressResponse {

    private Long userId;
    private Long courseId;
    private long totalLessons;
    private long completedLessons;
    private double progressPercentage;
    private String status;

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