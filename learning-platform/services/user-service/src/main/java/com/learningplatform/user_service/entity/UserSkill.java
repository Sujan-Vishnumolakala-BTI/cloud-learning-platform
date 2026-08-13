package com.learningplatform.user_service.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "user_skills",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_skill",
            columnNames = {"user_id", "skill"}
        )
    }
)
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String skill;

    @Column(nullable = false)
    private Integer proficiency;

    public UserSkill() {
    }

    public UserSkill(
            Long userId,
            String skill,
            Integer proficiency) {

        this.userId = userId;
        this.skill = skill;
        this.proficiency = proficiency;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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