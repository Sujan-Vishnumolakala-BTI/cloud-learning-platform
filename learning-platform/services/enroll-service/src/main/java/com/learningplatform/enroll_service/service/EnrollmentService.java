package com.learningplatform.enroll_service.service;

import com.learningplatform.enroll_service.dto.CourseResponse;
import com.learningplatform.enroll_service.dto.CreateEnrollmentRequest;
import com.learningplatform.enroll_service.dto.EnrollmentResponse;
import com.learningplatform.enroll_service.dto.InstructorStudentResponse;
import com.learningplatform.enroll_service.entity.Enrollment;
import com.learningplatform.enroll_service.entity.EnrollmentStatus;
import com.learningplatform.enroll_service.exception.AlreadyEnrolledException;
import com.learningplatform.enroll_service.exception.CourseNotAvailableException;
import com.learningplatform.enroll_service.repository.EnrollmentRepository;
import com.learningplatform.enroll_service.security.AuthenticatedUser;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import jakarta.servlet.http.HttpServletRequest;

// import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Map;

@Service
public class EnrollmentService {

        private final EnrollmentRepository enrollmentRepository;
        private final RestClient restClient;
        private final HttpServletRequest request;

        public EnrollmentService(
                        EnrollmentRepository enrollmentRepository,
                        RestClient restClient,
                        HttpServletRequest request) {

                this.enrollmentRepository = enrollmentRepository;

                this.restClient = restClient;

                this.request = request;
        }

        // @Transactional
        // public EnrollmentResponse enroll(
        // CreateEnrollmentRequest request) {

        // Long userId = getCurrentUserId();

        // Long courseId = request.getCourseId();

        // /*
        // * Validate course
        // */
        // CourseResponse course = getCourse(courseId);

        // /*
        // * Only active + published courses
        // */
        // if (!course.isActive() ||
        // !course.isPublished()) {

        // throw new CourseNotAvailableException(
        // "Course is not available for enrollment");
        // }

        // /*
        // * Prevent duplicate enrollment
        // */
        // if (enrollmentRepository
        // .existsByUserIdAndCourseId(
        // userId,
        // courseId)) {

        // throw new AlreadyEnrolledException(
        // "User is already enrolled in this course");
        // }

        // Enrollment enrollment = new Enrollment();

        // enrollment.setUserId(userId);
        // enrollment.setCourseId(courseId);
        // enrollment.setStatus(
        // EnrollmentStatus.ACTIVE);

        // Enrollment saved = enrollmentRepository.save(enrollment);

        // return toResponse(saved);
        // }

        @Transactional
        public EnrollmentResponse enroll(
                        CreateEnrollmentRequest request) {

                Long userId = getCurrentUserId();

                Long courseId = request.getCourseId();

                /*
                 * Check whether this user already has
                 * an enrollment for this course.
                 */
                Optional<Enrollment> existingEnrollment = enrollmentRepository
                                .findByUserIdAndCourseId(
                                                userId,
                                                courseId);

                if (existingEnrollment.isPresent()) {

                        Enrollment enrollment = existingEnrollment.get();

                        /*
                         * Already actively enrolled.
                         */
                        if (enrollment.getStatus() == EnrollmentStatus.ACTIVE) {

                                throw new AlreadyEnrolledException(
                                                "User is already enrolled in this course");
                        }

                        /*
                         * Previously cancelled.
                         *
                         * Reactivate the same enrollment.
                         */
                        if (enrollment.getStatus() == EnrollmentStatus.CANCELLED) {

                                enrollment.setStatus(
                                                EnrollmentStatus.ACTIVE);

                                enrollment.setEnrolledAt(
                                                LocalDateTime.now());

                                enrollment.setCompletedAt(null);

                                Enrollment saved = enrollmentRepository
                                                .save(enrollment);

                                return toResponse(saved);
                        }
                }

                /*
                 * No previous enrollment.
                 *
                 * Create a new one.
                 */
                Enrollment enrollment = new Enrollment();

                enrollment.setUserId(userId);
                enrollment.setCourseId(courseId);
                enrollment.setStatus(
                                EnrollmentStatus.ACTIVE);
                enrollment.setEnrolledAt(
                                LocalDateTime.now());
                enrollment.setCompletedAt(null);

                Enrollment saved = enrollmentRepository
                                .save(enrollment);

                return toResponse(saved);
        }

        private String getCurrentToken() {

                String authHeader = request.getHeader("Authorization");

                if (authHeader == null ||
                                !authHeader.startsWith("Bearer ")) {

                        throw new IllegalStateException(
                                        "JWT token not found");
                }

                return authHeader.substring(7);
        }

        public List<EnrollmentResponse> getMyEnrollments() {

                Long userId = getCurrentUserId();

                return enrollmentRepository
                                .findByUserIdOrderByEnrolledAtDesc(userId)
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        public EnrollmentResponse getEnrollment(
                        Long id) {

                Long userId = getCurrentUserId();

                Enrollment enrollment = enrollmentRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Enrollment not found"));

                /*
                 * User can only see own enrollment
                 */
                if (!enrollment.getUserId()
                                .equals(userId)) {

                        throw new RuntimeException(
                                        "You cannot access this enrollment");
                }

                return toResponse(enrollment);
        }

        @Transactional
        public void cancelEnrollment(Long id) {

                Long userId = getCurrentUserId();

                Enrollment enrollment = enrollmentRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Enrollment not found"));

                if (!enrollment.getUserId()
                                .equals(userId)) {

                        throw new RuntimeException(
                                        "You cannot modify this enrollment");
                }

                enrollment.setStatus(
                                EnrollmentStatus.CANCELLED);

                enrollmentRepository.save(enrollment);
        }

        // private CourseResponse getCourse(
        // Long courseId) {

        // try {

        // return restClient
        // .get()
        // .uri("/api/courses/{id}", courseId)
        // .retrieve()
        // .body(CourseResponse.class);

        // } catch (Exception ex) {

        // throw new CourseNotAvailableException(
        // "Course not found or unavailable");
        // }
        // }

        private CourseResponse getCourse(Long courseId) {

                try {

                        Authentication authentication = SecurityContextHolder
                                        .getContext()
                                        .getAuthentication();

                        String token = getCurrentToken();

                        return restClient
                                        .get()
                                        .uri(
                                                        "/api/courses/{id}",
                                                        courseId)
                                        .header(
                                                        "Authorization",
                                                        "Bearer " + token)
                                        .retrieve()
                                        .body(CourseResponse.class);

                } catch (Exception ex) {

                        System.out.println(
                                        "COURSE SERVICE ERROR: "
                                                        + ex.getMessage());

                        throw new CourseNotAvailableException(
                                        "Course not found or unavailable");
                }
        }

        private Long getCurrentUserId() {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null ||
                                !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {

                        throw new RuntimeException(
                                        "Authenticated user not found");
                }

                return user.getUserId();
        }

        private EnrollmentResponse toResponse(
                        Enrollment enrollment) {

                return new EnrollmentResponse(
                                enrollment.getId(),
                                enrollment.getUserId(),
                                enrollment.getCourseId(),
                                enrollment.getStatus(),
                                enrollment.getEnrolledAt(),
                                enrollment.getCompletedAt(),
                                enrollment.getUpdatedAt());
        }

        public List<InstructorStudentResponse> getStudentsByCourse(Long courseId) {

                return enrollmentRepository.findByCourseId(courseId)
                                .stream()
                                .map(enrollment -> new InstructorStudentResponse(
                                                enrollment.getUserId(),
                                                enrollment.getCourseId(),
                                                enrollment.getStatus(),
                                                enrollment.getEnrolledAt(),
                                                enrollment.getCompletedAt()))
                                .toList();
        }

        public Map<String, Long> getAdminEnrollmentStats() {

                long active = enrollmentRepository
                                .countByStatus(
                                                EnrollmentStatus.ACTIVE);

                long completed = enrollmentRepository
                                .countByStatus(
                                                EnrollmentStatus.COMPLETED);

                long cancelled = enrollmentRepository
                                .countByStatus(
                                                EnrollmentStatus.CANCELLED);

                long total = active +
                                completed +
                                cancelled;

                return Map.of(
                                "totalEnrollments", total,
                                "activeEnrollments", active,
                                "completedEnrollments", completed,
                                "cancelledEnrollments", cancelled);
        }

        public long getActiveEnrollmentCount() {

                return enrollmentRepository
                                .countByStatus(
                                                EnrollmentStatus.ACTIVE);
        }
}