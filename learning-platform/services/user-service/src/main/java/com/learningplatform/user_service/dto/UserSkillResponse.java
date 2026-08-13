package com.learningplatform.user_service.dto;

import java.util.List;

public class UserSkillResponse {

    private Long userId;
    private List<SkillResponse> skills;

    public UserSkillResponse(
            Long userId,
            List<SkillResponse> skills) {

        this.userId = userId;
        this.skills = skills;
    }

    public Long getUserId() {
        return userId;
    }

    public List<SkillResponse> getSkills() {
        return skills;
    }
}