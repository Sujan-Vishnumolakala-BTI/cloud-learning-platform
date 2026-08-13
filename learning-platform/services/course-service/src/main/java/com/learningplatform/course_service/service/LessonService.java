package com.learningplatform.course_service.service;

import com.learningplatform.course_service.dto.CourseLessonResponse;
import com.learningplatform.course_service.dto.CreateLessonRequest;
import com.learningplatform.course_service.dto.LessonResponse;
import com.learningplatform.course_service.dto.UpdateLessonRequest;
import com.learningplatform.course_service.entity.Course;
import com.learningplatform.course_service.entity.Lesson;
import com.learningplatform.course_service.entity.Module;
import com.learningplatform.course_service.exception.CourseNotFoundException;
import com.learningplatform.course_service.exception.LessonNotFoundException;
import com.learningplatform.course_service.exception.LessonOrderAlreadyExistsException;
import com.learningplatform.course_service.exception.ModuleNotFoundException;
import com.learningplatform.course_service.repository.CourseRepository;
import com.learningplatform.course_service.repository.LessonRepository;
import com.learningplatform.course_service.repository.ModuleRepository;
import com.learningplatform.course_service.security.AuthenticatedUser;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LessonService {

        private final LessonRepository lessonRepository;
        private final ModuleRepository moduleRepository;
        private final CourseRepository courseRepository;

        public LessonService(
                        LessonRepository lessonRepository,
                        ModuleRepository moduleRepository,
                        CourseRepository courseRepository) {

                this.lessonRepository = lessonRepository;
                this.moduleRepository = moduleRepository;
                this.courseRepository = courseRepository;
        }

        /*
         * CREATE LESSON
         */
        @Transactional
        public LessonResponse createLesson(
                        Long moduleId,
                        CreateLessonRequest request) {

                Module module = getModule(moduleId);

                Course course = getCourse(module.getCourseId());

                checkCourseOwnership(course);

                if (lessonRepository
                                .existsByModuleIdAndOrderIndex(
                                                moduleId,
                                                request.getOrderIndex())) {

                        throw new LessonOrderAlreadyExistsException(
                                        "A lesson with order " +
                                                        request.getOrderIndex() +
                                                        " already exists in this module");
                }

                Lesson lesson = new Lesson();

                lesson.setModuleId(moduleId);
                lesson.setTitle(request.getTitle());
                lesson.setDescription(
                                request.getDescription());
                lesson.setOrderIndex(
                                request.getOrderIndex());
                lesson.setContentType(
                                request.getContentType());
                lesson.setContentUrl(
                                request.getContentUrl());
                lesson.setDurationMinutes(
                                request.getDurationMinutes());

                Lesson saved = lessonRepository.save(lesson);

                return toResponse(saved);
        }

        /*
         * GET LESSONS FOR MODULE
         */
        public List<LessonResponse> getLessonsByModule(
                        Long moduleId) {

                if (!moduleRepository.existsById(moduleId)) {

                        throw new ModuleNotFoundException(
                                        "Module not found with id: " + moduleId);
                }

                return lessonRepository
                                .findByModuleIdOrderByOrderIndexAsc(moduleId)
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        public Long getCourseId(Long lessonId) {

                Lesson lesson = lessonRepository.findById(lessonId)
                                .orElseThrow(() -> new LessonNotFoundException(
                                                "Lesson not found with id: "
                                                                + lessonId));

                Module module = moduleRepository.findById(
                                lesson.getModuleId())
                                .orElseThrow(() -> new ModuleNotFoundException(
                                                "Module not found"));

                return module.getCourseId();
        }

        // public long getLessonCount(Long courseId) {

        // return moduleRepository
        // .findByCourseId(courseId)
        // .stream()
        // .mapToLong(module -> lessonRepository
        // .findByModuleId(
        // module.getId())
        // .size())
        // .sum();
        // }

        public long getLessonCount(Long courseId) {

                List<Module> modules = moduleRepository.findByCourseId(
                                courseId);

                long totalLessons = 0;

                for (Module module : modules) {

                        totalLessons += lessonRepository.countByModuleId(
                                        module.getId());
                }

                return totalLessons;
        }

        /*
         * GET LESSON
         */
        public LessonResponse getLesson(Long id) {

                Lesson lesson = lessonRepository.findById(id)
                                .orElseThrow(() -> new LessonNotFoundException(
                                                "Lesson not found with id: " + id));

                return toResponse(lesson);
        }

        /*
         * UPDATE LESSON
         */
        @Transactional
        public LessonResponse updateLesson(
                        Long id,
                        UpdateLessonRequest request) {

                Lesson lesson = lessonRepository.findById(id)
                                .orElseThrow(() -> new LessonNotFoundException(
                                                "Lesson not found with id: " + id));

                Module module = getModule(lesson.getModuleId());

                Course course = getCourse(module.getCourseId());

                checkCourseOwnership(course);

                boolean orderChanged = !lesson.getOrderIndex()
                                .equals(request.getOrderIndex());

                if (orderChanged &&
                                lessonRepository
                                                .existsByModuleIdAndOrderIndexAndIdNot(
                                                                lesson.getModuleId(),
                                                                request.getOrderIndex(),
                                                                lesson.getId())) {

                        throw new LessonOrderAlreadyExistsException(
                                        "A lesson with order " +
                                                        request.getOrderIndex() +
                                                        " already exists in this module");
                }

                lesson.setTitle(request.getTitle());
                lesson.setDescription(
                                request.getDescription());
                lesson.setOrderIndex(
                                request.getOrderIndex());
                lesson.setContentType(
                                request.getContentType());
                lesson.setContentUrl(
                                request.getContentUrl());
                lesson.setDurationMinutes(
                                request.getDurationMinutes());

                return toResponse(
                                lessonRepository.save(lesson));
        }

        /*
         * DELETE LESSON
         */
        @Transactional
        public void deleteLesson(Long id) {

                Lesson lesson = lessonRepository.findById(id)
                                .orElseThrow(() -> new LessonNotFoundException(
                                                "Lesson not found with id: " + id));

                Module module = getModule(lesson.getModuleId());

                Course course = getCourse(module.getCourseId());

                checkCourseOwnership(course);

                lessonRepository.delete(lesson);
        }

        private Module getModule(Long moduleId) {

                return moduleRepository.findById(moduleId)
                                .orElseThrow(() -> new ModuleNotFoundException(
                                                "Module not found with id: " + moduleId));
        }

        private Course getCourse(Long courseId) {

                return courseRepository.findById(courseId)
                                .orElseThrow(() -> new CourseNotFoundException(
                                                "Course not found with id: " + courseId));
        }

        /*
         * ADMIN:
         * allowed
         *
         * INSTRUCTOR:
         * own course only
         */
        private void checkCourseOwnership(
                        Course course) {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (hasRole("ROLE_ADMIN")) {
                        return;
                }

                if (!hasRole("ROLE_INSTRUCTOR")) {

                        throw new AccessDeniedException(
                                        "Only instructors or admins can manage lessons");
                }

                if (!(authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser)) {

                        throw new AccessDeniedException(
                                        "Invalid authenticated user");
                }

                Long currentUserId = authenticatedUser.getUserId();

                if (!course.getInstructorId()
                                .equals(currentUserId)) {

                        throw new AccessDeniedException(
                                        "You are not the owner of this course");
                }
        }

        private boolean hasRole(String role) {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null) {
                        return false;
                }

                return authentication.getAuthorities()
                                .stream()
                                .anyMatch(authority -> authority.getAuthority()
                                                .equals(role));
        }

        private LessonResponse toResponse(
                        Lesson lesson) {

                return new LessonResponse(
                                lesson.getId(),
                                lesson.getModuleId(),
                                lesson.getTitle(),
                                lesson.getDescription(),
                                lesson.getOrderIndex(),
                                lesson.getContentType(),
                                lesson.getContentUrl(),
                                lesson.getDurationMinutes(),
                                lesson.getCreatedAt(),
                                lesson.getUpdatedAt());
        }

        public List<CourseLessonResponse> getLessonsByCourse(
                        Long courseId) {

                if (!courseRepository.existsById(courseId)) {

                        throw new CourseNotFoundException(
                                        "Course not found with id: " + courseId);
                }

                List<Module> modules = moduleRepository
                                .findByCourseIdOrderByOrderIndexAsc(
                                                courseId);

                List<Long> moduleIds = modules.stream()
                                .map(Module::getId)
                                .toList();

                if (moduleIds.isEmpty()) {
                        return List.of();
                }

                return lessonRepository
                                .findByModuleIdInOrderByModuleIdAscOrderIndexAsc(
                                                moduleIds)
                                .stream()
                                .map(lesson -> new CourseLessonResponse(
                                                lesson.getId(),
                                                lesson.getModuleId(),
                                                lesson.getTitle(),
                                                lesson.getOrderIndex()))
                                .toList();
        }
}