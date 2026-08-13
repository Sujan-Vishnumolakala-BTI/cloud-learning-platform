package com.learningplatform.user_service.dto;

public class SkillResponse {

    private String skill;

    private Integer proficiency;

    public SkillResponse(
            String skill,
            Integer proficiency) {

        this.skill = skill;
        this.proficiency = proficiency;
    }

    public String getSkill() {
        return skill;
    }

    public Integer getProficiency() {
        return proficiency;
    }
}