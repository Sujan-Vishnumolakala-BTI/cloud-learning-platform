package com.learningplatform.course_service.repository;

import com.learningplatform.course_service.entity.Question;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository
        extends JpaRepository<Question, Long> {

    List<Question>
    findByQuizIdOrderByOrderIndexAsc(
            Long quizId);

    boolean existsByQuizIdAndOrderIndex(
            Long quizId,
            Integer orderIndex);

    boolean existsByQuizIdAndOrderIndexAndIdNot(
            Long quizId,
            Integer orderIndex,
            Long id);
}