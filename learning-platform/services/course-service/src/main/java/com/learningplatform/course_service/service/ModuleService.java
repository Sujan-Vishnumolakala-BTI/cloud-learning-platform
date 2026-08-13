package com.learningplatform.course_service.service;

import com.learningplatform.course_service.dto.CreateModuleRequest;
import com.learningplatform.course_service.dto.ModuleResponse;
import com.learningplatform.course_service.dto.UpdateModuleRequest;
import com.learningplatform.course_service.entity.Module;
import com.learningplatform.course_service.exception.CourseNotFoundException;
import com.learningplatform.course_service.exception.ModuleNotFoundException;
import com.learningplatform.course_service.exception.ModuleOrderAlreadyExistsException;
import com.learningplatform.course_service.repository.CourseRepository;
import com.learningplatform.course_service.repository.ModuleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    private final CourseRepository courseRepository;

    public ModuleService(
            ModuleRepository moduleRepository,
            CourseRepository courseRepository) {

        this.moduleRepository = moduleRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public ModuleResponse createModule(
            Long courseId,
            CreateModuleRequest request) {

        // Verify course exists
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException(
                    "Course not found");
        }

        // Prevent duplicate order
        if (moduleRepository
                .existsByCourseIdAndOrderIndex(
                        courseId,
                        request.getOrderIndex())) {

            throw new ModuleOrderAlreadyExistsException(
                    "A module with order " +
                            request.getOrderIndex() +
                            " already exists in this course");
        }

        Module module = new Module();

        module.setCourseId(courseId);
        module.setTitle(request.getTitle());
        module.setDescription(request.getDescription());
        module.setOrderIndex(request.getOrderIndex());

        Module saved = moduleRepository.save(module);

        return toResponse(saved);
    }

    public List<ModuleResponse> getModulesByCourse(
            Long courseId) {

        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException(
                    "Course not found");
        }

        return moduleRepository
                .findByCourseIdOrderByOrderIndexAsc(courseId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ModuleResponse getModule(Long id) {

        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ModuleNotFoundException(
                        "Module not found"));

        return toResponse(module);
    }

    @Transactional
    public ModuleResponse updateModule(
            Long id,
            UpdateModuleRequest request) {

        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ModuleNotFoundException(
                        "Module not found"));

        module.setTitle(request.getTitle());
        module.setDescription(
                request.getDescription());
        module.setOrderIndex(
                request.getOrderIndex());

        return toResponse(
                moduleRepository.save(module));
    }

    @Transactional
    public void deleteModule(Long id) {

        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ModuleNotFoundException(
                        "Module not found"));

        moduleRepository.delete(module);
    }

    private ModuleResponse toResponse(
            Module module) {

        return new ModuleResponse(
                module.getId(),
                module.getCourseId(),
                module.getTitle(),
                module.getDescription(),
                module.getOrderIndex(),
                module.getCreatedAt(),
                module.getUpdatedAt());
    }
}