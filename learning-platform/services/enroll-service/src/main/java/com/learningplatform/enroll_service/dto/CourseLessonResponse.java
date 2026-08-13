package com.learningplatform.enroll_service.dto;

public class CourseLessonResponse {

    private Long id;
    private Long moduleId;
    private String title;
    private Integer orderIndex;

    public Long getId() {
        return id;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public String getTitle() {
        return title;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }
}