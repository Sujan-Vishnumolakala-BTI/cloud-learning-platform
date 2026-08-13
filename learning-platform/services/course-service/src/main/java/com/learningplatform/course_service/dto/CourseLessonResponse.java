package com.learningplatform.course_service.dto;

public class CourseLessonResponse {

    private Long id;
    private Long moduleId;
    private String title;
    private Integer orderIndex;

    public CourseLessonResponse(
            Long id,
            Long moduleId,
            String title,
            Integer orderIndex) {

        this.id = id;
        this.moduleId = moduleId;
        this.title = title;
        this.orderIndex = orderIndex;
    }

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