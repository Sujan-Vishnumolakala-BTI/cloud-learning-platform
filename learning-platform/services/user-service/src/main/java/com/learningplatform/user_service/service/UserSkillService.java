package com.learningplatform.user_service.service;

import com.learningplatform.user_service.dto.SkillRequest;
import com.learningplatform.user_service.entity.UserSkill;
import com.learningplatform.user_service.repository.UserSkillRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSkillService {

    private final UserSkillRepository repository;

    public UserSkillService(
            UserSkillRepository repository) {

        this.repository = repository;
    }

    @Transactional
    public List<UserSkill> updateSkills(
            Long userId,
            List<SkillRequest> requests) {

        int deleted = repository.deleteByUserId(userId);

        System.out.println(
                "Deleted old skills: " + deleted);

        repository.flush();

        List<UserSkill> skills = requests.stream()
                .map(request -> {

                    UserSkill userSkill = new UserSkill();

                    userSkill.setUserId(userId);

                    userSkill.setSkill(
                            request.getSkill().trim());

                    userSkill.setProficiency(
                            request.getProficiency());

                    return userSkill;
                })
                .toList();

        return repository.saveAll(skills);
    }

    public List<UserSkill> getSkills(
            Long userId) {

        return repository.findByUserId(userId);
    }
}