package com.learningplatform.course_service.dto;

import com.learningplatform.course_service.entity.Course;

import java.time.LocalDateTime;

import java.util.Set;

public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private String category;
    private boolean published;
    private boolean active;
    private Long instructorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<String> skills;

    public CourseResponse() {
    }

    public CourseResponse(Course course) {

        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.category = course.getCategory();
        this.published = course.isPublished();
        this.active = course.isActive();
        this.instructorId = course.getInstructorId();
        this.createdAt = course.getCreatedAt();
        this.updatedAt = course.getUpdatedAt();
        this.skills = course.getSkills();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public boolean isPublished() {
        return published;
    }

    public boolean isActive() {
        return active;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }
}