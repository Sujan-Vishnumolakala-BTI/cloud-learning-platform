package com.learningplatform.user_service.repository;

import com.learningplatform.user_service.entity.UserSkill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserSkillRepository
        extends JpaRepository<UserSkill, Long> {

    List<UserSkill> findByUserId(Long userId);

    Optional<UserSkill> findByUserIdAndSkill(
            Long userId,
            String skill);

    @Modifying
    @Query("DELETE FROM UserSkill u WHERE u.userId = :userId")
    int deleteByUserId(@Param("userId") Long userId);
}