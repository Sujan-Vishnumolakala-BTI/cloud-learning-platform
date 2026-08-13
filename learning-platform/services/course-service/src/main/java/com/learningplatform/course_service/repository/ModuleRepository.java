package com.learningplatform.course_service.repository;

import com.learningplatform.course_service.dto.LessonResponse;
import com.learningplatform.course_service.entity.Module;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ModuleRepository
                extends JpaRepository<Module, Long> {

        List<Module> findByCourseIdOrderByOrderIndexAsc(
                        Long courseId);

        boolean existsByCourseIdAndOrderIndex(
                        Long courseId,
                        Integer orderIndex);

        List<Module> findByCourseId(
                        Long courseId);

}