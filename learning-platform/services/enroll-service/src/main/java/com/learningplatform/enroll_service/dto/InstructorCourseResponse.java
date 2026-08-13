package com.learningplatform.enroll_service.dto;

public class InstructorCourseResponse {

    private Long courseId;
    private String title;
    private String description;
    private Long instructorId;

    public InstructorCourseResponse(
            Long courseId,
            String title,
            String description,
            Long instructorId) {

        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Long getInstructorId() {
        return instructorId;
    }
}