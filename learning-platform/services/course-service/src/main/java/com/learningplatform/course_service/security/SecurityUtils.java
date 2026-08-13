package com.learningplatform.course_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class SecurityUtils {

    private final HttpServletRequest request;

    public SecurityUtils(HttpServletRequest request) {
        this.request = request;
    }

    public String getAuthorizationHeader() {

        return request.getHeader("Authorization");
    }
}