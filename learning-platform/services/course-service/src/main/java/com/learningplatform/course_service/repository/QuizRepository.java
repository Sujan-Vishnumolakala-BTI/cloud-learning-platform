package com.learningplatform.course_service.repository;

import com.learningplatform.course_service.entity.Quiz;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository
        extends JpaRepository<Quiz, Long> {

    Optional<Quiz> findByLessonId(Long lessonId);

    boolean existsByLessonId(Long lessonId);
    
    List<Quiz> findByLessonIdIn(List<Long> lessonIds);
}