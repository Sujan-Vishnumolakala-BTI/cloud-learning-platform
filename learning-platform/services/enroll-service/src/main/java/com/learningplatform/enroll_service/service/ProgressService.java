package com.learningplatform.enroll_service.service;

import com.learningplatform.enroll_service.dto.CourseLessonResponse;
import com.learningplatform.enroll_service.dto.CourseProgressResponse;
import com.learningplatform.enroll_service.dto.CourseStudentProgressResponse;
import com.learningplatform.enroll_service.dto.LessonProgressResponse;

import com.learningplatform.enroll_service.entity.Enrollment;
import com.learningplatform.enroll_service.entity.EnrollmentStatus;
import com.learningplatform.enroll_service.entity.LessonProgress;
import com.learningplatform.enroll_service.entity.ProgressStatus;

import com.learningplatform.enroll_service.repository.EnrollmentRepository;
import com.learningplatform.enroll_service.repository.LessonProgressRepository;

import com.learningplatform.enroll_service.security.AuthenticatedUser;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

import com.learningplatform.enroll_service.event.CourseCompletedEvent;
import com.learningplatform.enroll_service.event.CourseCompletionEventProducer;

@Service
public class ProgressService {

        private final LessonProgressRepository progressRepository;

        private final RestClient restClient;

        private final EnrollmentRepository enrollmentRepository;

        private final HttpServletRequest request;

        private final CourseClient courseClient;

        private final CourseCompletionEventProducer courseCompletionEventProducer;

        public ProgressService(
                        LessonProgressRepository progressRepository,
                        EnrollmentRepository enrollmentRepository,
                        RestClient restClient,
                        HttpServletRequest request,
                        CourseClient courseClient,
                        CourseCompletionEventProducer courseCompletionEventProducer) {

                this.progressRepository = progressRepository;

                this.enrollmentRepository = enrollmentRepository;

                this.restClient = restClient;

                this.request = request;

                this.courseClient = courseClient;

                this.courseCompletionEventProducer = courseCompletionEventProducer;
        }

        /*
         * =========================================================
         * START LESSON
         * =========================================================
         */

        // @Transactional
        // public LessonProgressResponse startLesson(
        // Long lessonId) {

        // Long userId = getCurrentUserId();

        // Long courseId = courseClient.getCourseIdForLesson(
        // lessonId);

        // /*
        // * Verify student is enrolled
        // */
        // Enrollment enrollment = enrollmentRepository
        // .findByUserIdAndCourseId(
        // userId,
        // courseId)
        // .orElseThrow(() -> new AccessDeniedException(
        // "You are not enrolled in this course"));

        // /*
        // * Only ACTIVE enrollment can
        // * start a lesson.
        // */
        // if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {

        // throw new AccessDeniedException(
        // "Your enrollment is not active");
        // }

        // LessonProgress progress = progressRepository
        // .findByUserIdAndLessonId(
        // userId,
        // lessonId)
        // .orElseGet(() -> {

        // LessonProgress newProgress = new LessonProgress();

        // newProgress.setUserId(
        // userId);

        // newProgress.setCourseId(
        // courseId);

        // newProgress.setLessonId(
        // lessonId);

        // newProgress.setStatus(
        // ProgressStatus.IN_PROGRESS);

        // newProgress.setStartedAt(
        // LocalDateTime.now());

        // return newProgress;
        // });

        // /*
        // * If lesson was previously NOT_STARTED,
        // * move it to IN_PROGRESS.
        // */
        // if (progress.getStatus() == ProgressStatus.NOT_STARTED) {

        // progress.setStatus(
        // ProgressStatus.IN_PROGRESS);

        // if (progress.getStartedAt() == null) {

        // progress.setStartedAt(
        // LocalDateTime.now());
        // }
        // }

        // /*
        // * Don't allow completed lesson
        // * to become IN_PROGRESS again.
        // */
        // if (progress.getStatus() == ProgressStatus.COMPLETED) {

        // return toResponse(progress);
        // }

        // /*
        // * Make sure course ID exists
        // * for old records.
        // */
        // if (progress.getCourseId() == null) {

        // progress.setCourseId(
        // courseId);
        // }

        // LessonProgress saved = progressRepository.save(
        // progress);

        // return toResponse(saved);
        // }

        // @Transactional
        // public LessonProgressResponse startLesson(Long lessonId) {

        // Long userId = getCurrentUserId();

        // System.out.println("========== START LESSON ==========");
        // System.out.println("USER ID: " + userId);
        // System.out.println("LESSON ID: " + lessonId);

        // Long courseId = courseClient.getCourseIdForLesson(lessonId);

        // System.out.println("COURSE ID FROM COURSE SERVICE: " + courseId);

        // Enrollment enrollment = enrollmentRepository
        // .findByUserIdAndCourseId(
        // userId,
        // courseId)
        // .orElseThrow(() -> {

        // System.out.println(
        // "❌ ENROLLMENT NOT FOUND");

        // System.out.println(
        // "USER ID: " + userId);

        // System.out.println(
        // "COURSE ID: " + courseId);

        // return new AccessDeniedException(
        // "You are not enrolled in this course");
        // });

        // System.out.println(
        // "✅ ENROLLMENT FOUND: " +
        // enrollment.getId());

        // System.out.println(
        // "ENROLLMENT USER ID: " +
        // enrollment.getUserId());

        // System.out.println(
        // "ENROLLMENT COURSE ID: " +
        // enrollment.getCourseId());

        // System.out.println(
        // "ENROLLMENT STATUS: " +
        // enrollment.getStatus());

        // if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {

        // System.out.println(
        // "❌ ENROLLMENT IS NOT ACTIVE");

        // throw new AccessDeniedException(
        // "Your enrollment is not active");
        // }

        // System.out.println(
        // "✅ ENROLLMENT ACTIVE");

        // LessonProgress progress = progressRepository
        // .findByUserIdAndLessonId(
        // userId,
        // lessonId)
        // .orElseGet(() -> {

        // LessonProgress newProgress = new LessonProgress();

        // newProgress.setUserId(userId);
        // newProgress.setCourseId(courseId);
        // newProgress.setLessonId(lessonId);
        // newProgress.setStatus(
        // ProgressStatus.IN_PROGRESS);
        // newProgress.setStartedAt(
        // LocalDateTime.now());

        // return newProgress;
        // });

        // if (progress.getStatus() == ProgressStatus.NOT_STARTED) {

        // progress.setStatus(
        // ProgressStatus.IN_PROGRESS);

        // if (progress.getStartedAt() == null) {

        // progress.setStartedAt(
        // LocalDateTime.now());
        // }
        // }

        // if (progress.getStatus() == ProgressStatus.COMPLETED) {

        // return toResponse(progress);
        // }

        // if (progress.getCourseId() == null) {

        // progress.setCourseId(courseId);
        // }

        // LessonProgress saved = progressRepository.save(progress);

        // System.out.println(
        // "✅ LESSON STARTED SUCCESSFULLY");

        // System.out.println(
        // "================================");

        // return toResponse(saved);
        // }

        @Transactional
        public LessonProgressResponse startLesson(Long lessonId) {

                Long userId = getCurrentUserId();

                System.out.println("========== START LESSON ==========");
                System.out.println("USER ID: " + userId);
                System.out.println("LESSON ID: " + lessonId);

                /*
                 * Get course ID from Course Service.
                 */
                Long courseId = courseClient.getCourseIdForLesson(lessonId);

                System.out.println(
                                "COURSE ID FROM COURSE SERVICE: " + courseId);

                /*
                 * Find student's enrollment.
                 */
                Enrollment enrollment = enrollmentRepository
                                .findByUserIdAndCourseId(
                                                userId,
                                                courseId)
                                .orElseThrow(() -> {

                                        System.out.println(
                                                        "❌ ENROLLMENT NOT FOUND");

                                        System.out.println(
                                                        "USER ID: " + userId);

                                        System.out.println(
                                                        "COURSE ID: " + courseId);

                                        return new AccessDeniedException(
                                                        "You are not enrolled in this course");
                                });

                System.out.println(
                                "✅ ENROLLMENT FOUND: " +
                                                enrollment.getId());

                System.out.println(
                                "ENROLLMENT STATUS: " +
                                                enrollment.getStatus());

                /*
                 * =========================================================
                 * HANDLE COMPLETED ENROLLMENT
                 * =========================================================
                 *
                 * A student may have completed the course previously.
                 *
                 * However, the instructor may have added new lessons
                 * after that.
                 *
                 * Example:
                 *
                 * Old course:
                 * 1 lesson
                 * 1 completed
                 * 100%
                 * COMPLETED
                 *
                 * Instructor adds another lesson:
                 * 2 total lessons
                 * 1 completed
                 * 50%
                 *
                 * In that situation we reactivate the enrollment.
                 */

                if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {

                        long totalLessons = courseClient.getLessonCount(courseId);

                        long completedLessons = progressRepository
                                        .countByUserIdAndCourseIdAndStatus(
                                                        userId,
                                                        courseId,
                                                        ProgressStatus.COMPLETED);

                        System.out.println(
                                        "TOTAL LESSONS: " + totalLessons);

                        System.out.println(
                                        "COMPLETED LESSONS: " + completedLessons);

                        /*
                         * New lessons have been added after
                         * the student completed the course.
                         */
                        if (totalLessons > completedLessons) {

                                System.out.println(
                                                "⚠️ NEW LESSONS DETECTED");

                                System.out.println(
                                                "REACTIVATING ENROLLMENT");

                                enrollment.setStatus(
                                                EnrollmentStatus.ACTIVE);

                                enrollment.setCompletedAt(null);

                                enrollmentRepository.save(enrollment);

                                System.out.println(
                                                "✅ ENROLLMENT REACTIVATED");

                        } else {

                                /*
                                 * No new lessons exist.
                                 *
                                 * The course is still completely finished.
                                 */
                                System.out.println(
                                                "❌ COURSE IS STILL COMPLETED");

                                return getExistingCompletedProgress(
                                                userId,
                                                lessonId);
                        }
                }

                /*
                 * Only ACTIVE enrollment can
                 * start a lesson.
                 */
                if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {

                        System.out.println(
                                        "❌ ENROLLMENT IS NOT ACTIVE");

                        throw new AccessDeniedException(
                                        "Your enrollment is not active");
                }

                System.out.println(
                                "✅ ENROLLMENT ACTIVE");

                /*
                 * Find existing lesson progress.
                 */
                LessonProgress progress = progressRepository
                                .findByUserIdAndLessonId(
                                                userId,
                                                lessonId)
                                .orElseGet(() -> {

                                        LessonProgress newProgress = new LessonProgress();

                                        newProgress.setUserId(userId);

                                        newProgress.setCourseId(courseId);

                                        newProgress.setLessonId(lessonId);

                                        newProgress.setStatus(
                                                        ProgressStatus.IN_PROGRESS);

                                        newProgress.setStartedAt(
                                                        LocalDateTime.now());

                                        return newProgress;
                                });

                /*
                 * If lesson was previously NOT_STARTED,
                 * move it to IN_PROGRESS.
                 */
                if (progress.getStatus() == ProgressStatus.NOT_STARTED) {

                        progress.setStatus(
                                        ProgressStatus.IN_PROGRESS);

                        if (progress.getStartedAt() == null) {

                                progress.setStartedAt(
                                                LocalDateTime.now());
                        }
                }

                /*
                 * Don't restart a completed lesson.
                 */
                if (progress.getStatus() == ProgressStatus.COMPLETED) {

                        return toResponse(progress);
                }

                /*
                 * Make sure course ID exists for
                 * old lesson progress records.
                 */
                if (progress.getCourseId() == null) {

                        progress.setCourseId(courseId);
                }

                LessonProgress saved = progressRepository.save(progress);

                System.out.println(
                                "✅ LESSON STARTED SUCCESSFULLY");

                System.out.println(
                                "================================");

                return toResponse(saved);
        }

        private LessonProgressResponse getExistingCompletedProgress(
        Long userId,
        Long lessonId) {

    return progressRepository
            .findByUserIdAndLessonId(
                    userId,
                    lessonId
            )
            .map(this::toResponse)
            .orElseThrow(() ->
                    new AccessDeniedException(
                            "Your enrollment is already completed"
                    )
            );
}

        /*
         * =========================================================
         * COMPLETE LESSON
         * =========================================================
         */

        @Transactional
        public LessonProgressResponse completeLesson(
                        Long lessonId) {

                Long userId = getCurrentUserId();

                /*
                 * Get course ID from Course Service.
                 */
                Long courseId = courseClient.getCourseIdForLesson(
                                lessonId);

                /*
                 * Verify that student is enrolled.
                 */
                Enrollment enrollment = enrollmentRepository
                                .findByUserIdAndCourseId(
                                                userId,
                                                courseId)
                                .orElseThrow(() -> new AccessDeniedException(
                                                "You are not enrolled in this course"));

                /*
                 * Only ACTIVE enrollments can
                 * complete lessons.
                 */
                if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {

                        throw new AccessDeniedException(
                                        "Your enrollment is not active");
                }

                LessonProgress progress = progressRepository
                                .findByUserIdAndLessonId(
                                                userId,
                                                lessonId)
                                .orElseGet(() -> {

                                        LessonProgress newProgress = new LessonProgress();

                                        newProgress.setUserId(
                                                        userId);

                                        newProgress.setCourseId(
                                                        courseId);

                                        newProgress.setLessonId(
                                                        lessonId);

                                        newProgress.setStatus(
                                                        ProgressStatus.IN_PROGRESS);

                                        newProgress.setStartedAt(
                                                        LocalDateTime.now());

                                        return newProgress;
                                });

                /*
                 * Make sure course ID exists
                 * for old records.
                 */
                if (progress.getCourseId() == null) {

                        progress.setCourseId(
                                        courseId);
                }

                /*
                 * Mark lesson as completed.
                 */
                progress.setStatus(
                                ProgressStatus.COMPLETED);

                /*
                 * If lesson was never started,
                 * create the start timestamp.
                 */
                if (progress.getStartedAt() == null) {

                        progress.setStartedAt(
                                        LocalDateTime.now());
                }

                /*
                 * Set completion timestamp.
                 */
                progress.setCompletedAt(
                                LocalDateTime.now());

                LessonProgress saved = progressRepository.save(
                                progress);

                /*
                 * Check whether the entire course
                 * has now been completed.
                 */
                completeEnrollmentIfCourseCompleted(
                                userId,
                                courseId);

                return toResponse(saved);
        }

        /*
         * =========================================================
         * COMPLETE ENROLLMENT WHEN COURSE IS 100%
         * =========================================================
         */

        private void completeEnrollmentIfCourseCompleted(
                        Long userId,
                        Long courseId) {

                /*
                 * Get total number of lessons
                 * from Course Service.
                 */
                long totalLessons = courseClient.getLessonCount(
                                courseId);

                /*
                 * Count completed lessons for
                 * this student and course.
                 */
                long completedLessons = progressRepository
                                .countByUserIdAndCourseIdAndStatus(
                                                userId,
                                                courseId,
                                                ProgressStatus.COMPLETED);

                /*
                 * No lessons means there is
                 * nothing to complete.
                 */
                if (totalLessons <= 0) {

                        return;
                }

                /*
                 * Course isn't completed yet.
                 */
                if (completedLessons < totalLessons) {

                        return;
                }

                /*
                 * Find enrollment.
                 */
                Enrollment enrollment = enrollmentRepository
                                .findByUserIdAndCourseId(
                                                userId,
                                                courseId)
                                .orElse(null);

                if (enrollment == null) {

                        return;
                }

                /*
                 * Only ACTIVE enrollment should
                 * transition to COMPLETED.
                 */
                if (enrollment.getStatus() == EnrollmentStatus.ACTIVE) {

                        enrollment.setStatus(
                                        EnrollmentStatus.COMPLETED);

                        enrollment.setCompletedAt(
                                        LocalDateTime.now());

                        enrollmentRepository.save(
                                        enrollment);

                        CourseCompletedEvent event = new CourseCompletedEvent(
                                        userId,
                                        courseId,
                                        enrollment.getCompletedAt());

                        courseCompletionEventProducer
                                        .publishCourseCompleted(event);
                }
        }

        /*
         * =========================================================
         * GET MY PROGRESS
         * =========================================================
         */

        public List<LessonProgressResponse> getMyProgress() {

                Long userId = getCurrentUserId();

                return progressRepository
                                .findByUserId(userId)
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        /*
         * =========================================================
         * GET COURSE PROGRESS
         * =========================================================
         */

        public CourseProgressResponse getCourseProgress(
                        Long courseId) {

                Long userId = getCurrentUserId();

                /*
                 * Verify active enrollment.
                 */
                boolean enrolled = enrollmentRepository
                                .existsByUserIdAndCourseIdAndStatus(
                                                userId,
                                                courseId,
                                                EnrollmentStatus.ACTIVE);

                /*
                 * Also allow a completed enrollment
                 * to view its progress.
                 */
                if (!enrolled) {

                        boolean completedEnrollment = enrollmentRepository
                                        .existsByUserIdAndCourseIdAndStatus(
                                                        userId,
                                                        courseId,
                                                        EnrollmentStatus.COMPLETED);

                        if (!completedEnrollment) {

                                throw new AccessDeniedException(
                                                "User is not enrolled in this course");
                        }
                }

                /*
                 * Get all lessons from Course Service.
                 */
                CourseLessonResponse[] lessons = restClient
                                .get()
                                .uri(
                                                "/api/courses/{courseId}/lessons",
                                                courseId)
                                .header(
                                                "Authorization",
                                                "Bearer " +
                                                                getCurrentToken())
                                .retrieve()
                                .body(
                                                CourseLessonResponse[].class);

                /*
                 * Course has no lessons.
                 */
                if (lessons == null ||
                                lessons.length == 0) {

                        return new CourseProgressResponse(
                                        courseId,
                                        userId,
                                        0,
                                        0,
                                        0.0,
                                        "NOT_STARTED");
                }

                long totalLessons = lessons.length;

                long completedLessons = 0;

                /*
                 * Check every lesson.
                 */
                for (CourseLessonResponse lesson : lessons) {

                        boolean completed = progressRepository
                                        .findByUserIdAndLessonId(
                                                        userId,
                                                        lesson.getId())
                                        .map(progress -> progress.getStatus() == ProgressStatus.COMPLETED)
                                        .orElse(false);

                        if (completed) {

                                completedLessons++;
                        }
                }

                /*
                 * Calculate percentage.
                 */
                double percentage = ((double) completedLessons
                                / totalLessons)
                                * 100;

                String status;

                if (completedLessons == 0) {

                        status = "NOT_STARTED";

                } else if (completedLessons == totalLessons) {

                        status = "COMPLETED";

                } else {

                        status = "IN_PROGRESS";
                }

                return new CourseProgressResponse(
                                courseId,
                                userId,
                                totalLessons,
                                completedLessons,
                                percentage,
                                status);
        }

        /*
         * =========================================================
         * GET CURRENT USER ID
         * =========================================================
         */

        private Long getCurrentUserId() {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null ||
                                !authentication.isAuthenticated()) {

                        throw new AccessDeniedException(
                                        "User is not authenticated");
                }

                if (!(authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser)) {

                        throw new AccessDeniedException(
                                        "Invalid authenticated user");
                }

                return authenticatedUser.getUserId();
        }

        /*
         * =========================================================
         * ENTITY -> RESPONSE
         * =========================================================
         */

        private LessonProgressResponse toResponse(
                        LessonProgress progress) {

                return new LessonProgressResponse(
                                progress.getId(),
                                progress.getUserId(),
                                progress.getLessonId(),
                                progress.getStatus(),
                                progress.getStartedAt(),
                                progress.getCompletedAt(),
                                progress.getCreatedAt(),
                                progress.getUpdatedAt());
        }

        /*
         * =========================================================
         * GET CURRENT JWT
         * =========================================================
         */

        private String getCurrentToken() {

                String authHeader = request.getHeader(
                                "Authorization");

                if (authHeader == null ||
                                !authHeader.startsWith(
                                                "Bearer ")) {

                        throw new AccessDeniedException(
                                        "JWT token not found");
                }

                return authHeader.substring(7);
        }

        // public List<CourseStudentProgressResponse> getInstructorCourseProgress(Long
        // courseId) {

        // List<Enrollment> enrollments = enrollmentRepository
        // .findByCourseId(courseId);

        // CourseLessonResponse[] lessons = restClient
        // .get()
        // .uri(
        // "/api/courses/{courseId}/lessons",
        // courseId)
        // .header(
        // "Authorization",
        // "Bearer " + getCurrentToken())
        // .retrieve()
        // .body(
        // CourseLessonResponse[].class);

        // long totalLessons = lessons == null
        // ? 0
        // : lessons.length;

        // List<CourseStudentProgressResponse> result = new java.util.ArrayList<>();

        // for (Enrollment enrollment : enrollments) {

        // Long userId = enrollment.getUserId();

        // long completedLessons = progressRepository
        // .findByUserIdAndCourseId(
        // userId,
        // courseId)
        // .stream()
        // .filter(progress -> progress.getStatus() == ProgressStatus.COMPLETED)
        // .count();

        // double percentage = totalLessons == 0
        // ? 0.0
        // : ((double) completedLessons
        // / totalLessons) * 100.0;

        // String status;

        // if (completedLessons == 0) {

        // status = "NOT_STARTED";

        // } else if (completedLessons >= totalLessons) {

        // status = "COMPLETED";

        // } else {

        // status = "IN_PROGRESS";
        // }

        // result.add(
        // new CourseStudentProgressResponse(
        // userId,
        // courseId,
        // totalLessons,
        // completedLessons,
        // percentage,
        // status));
        // }

        // return result;
        // }

        public List<CourseStudentProgressResponse> getInstructorCourseProgress(
                        Long courseId) {

                List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);

                /*
                 * Get total lesson count from Course Service.
                 *
                 * We already use this CourseClient method elsewhere
                 * in this service, so we don't need to call
                 * /api/courses/{courseId}/lessons here.
                 */
                long totalLessons = courseClient.getLessonCount(courseId);

                List<CourseStudentProgressResponse> result = new java.util.ArrayList<>();

                for (Enrollment enrollment : enrollments) {

                        Long userId = enrollment.getUserId();

                        long completedLessons = progressRepository
                                        .findByUserIdAndCourseId(
                                                        userId,
                                                        courseId)
                                        .stream()
                                        .filter(progress -> progress.getStatus() == ProgressStatus.COMPLETED)
                                        .count();

                        double percentage = totalLessons == 0
                                        ? 0.0
                                        : ((double) completedLessons
                                                        / totalLessons) * 100.0;

                        String status;

                        if (completedLessons == 0) {

                                status = "NOT_STARTED";

                        } else if (completedLessons >= totalLessons) {

                                status = "COMPLETED";

                        } else {

                                status = "IN_PROGRESS";
                        }

                        result.add(
                                        new CourseStudentProgressResponse(
                                                        userId,
                                                        courseId,
                                                        totalLessons,
                                                        completedLessons,
                                                        percentage,
                                                        status));
                }

                return result;
        }
}