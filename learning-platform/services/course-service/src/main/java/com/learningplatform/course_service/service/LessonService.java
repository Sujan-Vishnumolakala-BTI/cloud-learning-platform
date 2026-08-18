package com.learningplatform.course_service.service;

import com.learningplatform.course_service.dto.CourseLessonResponse;
import com.learningplatform.course_service.dto.CreateLessonRequest;
import com.learningplatform.course_service.dto.LessonResponse;
import com.learningplatform.course_service.dto.UpdateLessonRequest;
import com.learningplatform.course_service.dto.VideoUploadUrlResponse;
import com.learningplatform.course_service.dto.VideoUrlResponse;
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

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpRange;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.InputStream;

import java.util.List;

@Service
public class LessonService {

        private final LessonRepository lessonRepository;
        private final ModuleRepository moduleRepository;
        private final CourseRepository courseRepository;
        private final MinioService minioService;

        public LessonService(
                        LessonRepository lessonRepository,
                        ModuleRepository moduleRepository,
                        CourseRepository courseRepository,
                        MinioService minioService) {

                this.lessonRepository = lessonRepository;
                this.moduleRepository = moduleRepository;
                this.courseRepository = courseRepository;
                this.minioService = minioService;
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

        public VideoUploadUrlResponse generateVideoUploadUrl(
                        Long lessonId) {

                Lesson lesson = lessonRepository.findById(lessonId)
                                .orElseThrow(() -> new LessonNotFoundException(
                                                "Lesson not found with id: " + lessonId));

                Module module = getModule(lesson.getModuleId());

                Course course = getCourse(module.getCourseId());

                checkCourseOwnership(course);

                String objectKey = "course-" + course.getId()
                                + "/module-" + module.getId()
                                + "/lesson-" + lesson.getId()
                                + ".mp4";

                try {

                        String uploadUrl = minioService.generateUploadUrl(objectKey);

                        return new VideoUploadUrlResponse(
                                        lesson.getId(),
                                        objectKey,
                                        uploadUrl);

                } catch (Exception e) {

                        throw new RuntimeException(
                                        "Failed to generate video upload URL",
                                        e);
                }
        }

        @Transactional
        public LessonResponse completeVideoUpload(
                        Long lessonId,
                        String objectKey) {

                Lesson lesson = lessonRepository.findById(lessonId)
                                .orElseThrow(() -> new LessonNotFoundException(
                                                "Lesson not found with id: " + lessonId));

                Module module = getModule(lesson.getModuleId());

                Course course = getCourse(module.getCourseId());

                checkCourseOwnership(course);

                String expectedPrefix = "course-" + course.getId()
                                + "/module-" + module.getId()
                                + "/lesson-" + lesson.getId();

                if (!objectKey.startsWith(expectedPrefix)) {
                        throw new AccessDeniedException(
                                        "Invalid video object key");
                }

                lesson.setContentType("VIDEO");
                lesson.setContentUrl(objectKey);

                return toResponse(
                                lessonRepository.save(lesson));
        }

        public VideoUrlResponse getVideoUrl(Long lessonId) {

                Lesson lesson = lessonRepository.findById(lessonId)
                                .orElseThrow(() -> new LessonNotFoundException(
                                                "Lesson not found with id: " + lessonId));

                if (!"VIDEO".equalsIgnoreCase(lesson.getContentType())) {
                        throw new IllegalStateException(
                                        "Lesson does not contain a video");
                }

                if (lesson.getContentUrl() == null ||
                                lesson.getContentUrl().isBlank()) {

                        throw new IllegalStateException(
                                        "Video is not available for this lesson");
                }

                try {

                        String videoUrl = minioService.generateDownloadUrl(
                                        lesson.getContentUrl());

                        return new VideoUrlResponse(
                                        lesson.getId(),
                                        videoUrl);

                } catch (Exception e) {

                        throw new RuntimeException(
                                        "Failed to generate video URL",
                                        e);
                }
        }

        // public ResponseEntity<InputStreamResource> streamVideo(Long lessonId) {

        //         Lesson lesson = lessonRepository.findById(lessonId)
        //                         .orElseThrow(() -> new LessonNotFoundException(
        //                                         "Lesson not found with id: " + lessonId));

        //         if (!"VIDEO".equalsIgnoreCase(lesson.getContentType())) {
        //                 throw new IllegalStateException(
        //                                 "Lesson does not contain a video");
        //         }

        //         if (lesson.getContentUrl() == null ||
        //                         lesson.getContentUrl().isBlank()) {

        //                 throw new IllegalStateException(
        //                                 "Video is not available for this lesson");
        //         }

        //         try {

        //                 InputStream inputStream = minioService.getObject(
        //                                 lesson.getContentUrl());

        //                 return ResponseEntity.ok()
        //                                 .header(
        //                                                 HttpHeaders.CONTENT_DISPOSITION,
        //                                                 "inline; filename=\"lesson-" +
        //                                                                 lessonId +
        //                                                                 ".mp4\"")
        //                                 .contentType(MediaType.parseMediaType("video/mp4"))
        //                                 .body(new InputStreamResource(inputStream));

        //         } catch (Exception e) {

        //                 throw new RuntimeException(
        //                                 "Failed to stream video",
        //                                 e);
        //         }
        // }

        public ResponseEntity<InputStreamResource> streamVideo(
        Long lessonId,
        String rangeHeader) {

    Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new LessonNotFoundException(
                    "Lesson not found with id: " + lessonId));

    if (!"VIDEO".equalsIgnoreCase(lesson.getContentType())) {
        throw new IllegalStateException(
                "Lesson does not contain a video");
    }

    if (lesson.getContentUrl() == null ||
            lesson.getContentUrl().isBlank()) {

        throw new IllegalStateException(
                "Video is not available for this lesson");
    }

    try {

        var stat = minioService.statObject(
                lesson.getContentUrl());

        long fileSize = stat.size();

        /*
         * No Range header:
         * Return the complete video.
         */
        if (rangeHeader == null || rangeHeader.isBlank()) {

            InputStream inputStream =
                    minioService.getObject(
                            lesson.getContentUrl());

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"lesson-" +
                                    lessonId +
                                    ".mp4\"")
                    .header(
                            HttpHeaders.ACCEPT_RANGES,
                            "bytes")
                    .contentLength(fileSize)
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .body(new InputStreamResource(inputStream));
        }

        /*
         * Parse Range header.
         */
        HttpRange range = HttpRange.parseRanges(rangeHeader)
                .get(0);

        long start = range.getRangeStart(fileSize);
        long end = range.getRangeEnd(fileSize);

        long contentLength = end - start + 1;

        /*
         * For now, retrieve the object and skip
         * to the requested starting position.
         */
        InputStream inputStream =
                minioService.getObject(
                        lesson.getContentUrl());

        inputStream.skip(start);

        byte[] buffer = inputStream.readNBytes(
                (int) contentLength);

        InputStreamResource resource =
                new InputStreamResource(
                        new ByteArrayInputStream(buffer));

        return ResponseEntity.status(206)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"lesson-" +
                                lessonId +
                                ".mp4\"")
                .header(
                        HttpHeaders.ACCEPT_RANGES,
                        "bytes")
                .header(
                        HttpHeaders.CONTENT_RANGE,
                        "bytes " + start + "-" +
                                end + "/" + fileSize)
                .contentLength(buffer.length)
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(resource);

    } catch (Exception e) {

        throw new RuntimeException(
                "Failed to stream video",
                e);
    }
}
}