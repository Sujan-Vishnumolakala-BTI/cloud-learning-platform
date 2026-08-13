package com.learningplatform.user_service.controller;

import com.learningplatform.user_service.dto.SkillResponse;
import com.learningplatform.user_service.dto.UpdateSkillsRequest;
import com.learningplatform.user_service.dto.UserSkillResponse;
import com.learningplatform.user_service.entity.UserSkill;
import com.learningplatform.user_service.service.UserSkillService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserSkillController {

    private final UserSkillService userSkillService;

    public UserSkillController(
            UserSkillService userSkillService) {

        this.userSkillService = userSkillService;
    }

    @PutMapping("/{userId}/skills")
    public ResponseEntity<List<SkillResponse>> updateSkills(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateSkillsRequest request) {

        List<UserSkill> skills =
                userSkillService.updateSkills(
                        userId,
                        request.getSkills()
                );

        List<SkillResponse> response =
                skills.stream()
                        .map(skill ->
                                new SkillResponse(
                                        skill.getSkill(),
                                        skill.getProficiency()
                                )
                        )
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/skills")
    public ResponseEntity<List<SkillResponse>> getSkills(
            @PathVariable Long userId) {

        List<SkillResponse> response =
                userSkillService
                        .getSkills(userId)
                        .stream()
                        .map(skill ->
                                new SkillResponse(
                                        skill.getSkill(),
                                        skill.getProficiency()
                                )
                        )
                        .toList();

        return ResponseEntity.ok(response);
    }
}