package com.learningplatform.course_service.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/courses/public")
    public String publicTest() {

        return "Course Service public endpoint";
    }

    @GetMapping("/api/courses/test")
    public String authenticatedTest(
            Authentication authentication) {

        return "Authenticated as: "
                + authentication.getName()
                + " | Authorities: "
                + authentication.getAuthorities();
    }
}