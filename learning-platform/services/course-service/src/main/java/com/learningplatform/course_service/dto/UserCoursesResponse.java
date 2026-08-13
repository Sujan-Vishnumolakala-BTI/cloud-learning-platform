package com.learningplatform.course_service.dto;

import java.util.List;

public class UserCoursesResponse {

    private UserResponse user;
    private List<CourseResponse> courses;

    public UserCoursesResponse() {
    }

    public UserCoursesResponse(
            UserResponse user,
            List<CourseResponse> courses) {

        this.user = user;
        this.courses = courses;
    }

    public UserResponse getUser() {
        return user;
    }

    public List<CourseResponse> getCourses() {
        return courses;
    }
}