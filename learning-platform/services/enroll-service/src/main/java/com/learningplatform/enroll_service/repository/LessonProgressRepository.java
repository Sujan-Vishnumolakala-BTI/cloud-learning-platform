package com.learningplatform.enroll_service.repository;

import com.learningplatform.enroll_service.entity.LessonProgress;
import com.learningplatform.enroll_service.entity.ProgressStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository
        extends JpaRepository<LessonProgress, Long> {

    Optional<LessonProgress> findByUserIdAndLessonId(
            Long userId,
            Long lessonId);

    List<LessonProgress> findByUserId(
            Long userId);

    List<LessonProgress> findByUserIdAndCourseId(
            Long userId,
            Long courseId);

    long countByUserIdAndCourseIdAndStatus(
            Long userId,
            Long courseId,
            ProgressStatus status);
}