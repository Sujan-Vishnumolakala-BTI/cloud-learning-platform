package com.learningplatform.user_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SkillRequest {

    @NotBlank(message = "Skill is required")
    private String skill;

    @NotNull(message = "Proficiency is required")
    @Min(value = 1, message = "Proficiency must be at least 1")
    @Max(value = 10, message = "Proficiency must not exceed 10")
    private Integer proficiency;

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public Integer getProficiency() {
        return proficiency;
    }

    public void setProficiency(Integer proficiency) {
        this.proficiency = proficiency;
    }
}