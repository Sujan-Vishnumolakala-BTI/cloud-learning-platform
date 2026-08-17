package com.learningplatform.course_service.event;

import java.util.Set;

public class CourseEvent {

    private Long courseId;
    private String eventType;

    private String title;
    private String description;
    private String category;

    private Set<String> skills;

    private Long instructorId;

    private boolean published;
    private boolean active;

    public CourseEvent() {
    }

    public CourseEvent(
            Long courseId,
            String eventType,
            String title,
            String description,
            String category,
            Set<String> skills,
            Long instructorId,
            boolean published,
            boolean active) {

        this.courseId = courseId;
        this.eventType = eventType;
        this.title = title;
        this.description = description;
        this.category = category;
        this.skills = skills;
        this.instructorId = instructorId;
        this.published = published;
        this.active = active;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getEventType() {
        return eventType;
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

    public Set<String> getSkills() {
        return skills;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public boolean isPublished() {
        return published;
    }

    public boolean isActive() {
        return active;
    }
}