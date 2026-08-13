package com.learningplatform.user_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class UserSkillRequest {

    @NotEmpty(message = "At least one skill is required")
    @Valid
    private List<SkillRequest> skills;

    public List<SkillRequest> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillRequest> skills) {
        this.skills = skills;
    }
}